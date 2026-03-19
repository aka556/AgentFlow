package org.xiaoyu.xchatmind.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.xiaoyu.xchatmind.agent.tools.Tool;
import org.xiaoyu.xchatmind.config.ChatClientRegistry;
import org.xiaoyu.xchatmind.converter.AgentConverter;
import org.xiaoyu.xchatmind.converter.ChatMessageConverter;
import org.xiaoyu.xchatmind.converter.KnowledgeBaseConverter;
import org.xiaoyu.xchatmind.mapper.AgentMapper;
import org.xiaoyu.xchatmind.mapper.KnowledgeBaseMapper;
import org.xiaoyu.xchatmind.model.dto.AgentDTO;
import org.xiaoyu.xchatmind.model.dto.ChatMessageDTO;
import org.xiaoyu.xchatmind.model.dto.KnowledgeBaseDTO;
import org.xiaoyu.xchatmind.model.entity.Agent;
import org.xiaoyu.xchatmind.model.entity.KnowledgeBase;
import org.xiaoyu.xchatmind.service.ChatMessageFacadeService;
import org.xiaoyu.xchatmind.service.SseService;
import org.xiaoyu.xchatmind.service.ToolFacadeService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChatMindFactory {
    private final ChatClientRegistry chatClientRegistry;
    private final SseService sseService;
    private final AgentMapper agentMapper;
    private final AgentConverter agentConverter;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final KnowledgeBaseConverter knowledgeBaseConverter;
    private final ToolFacadeService toolFacadeService;
    private final ChatMessageFacadeService chatMessageFacadeService;
    private final ChatMessageConverter chatMessageConverter;

    // 运行时agent配置
    private AgentDTO agentConfig;

    public ChatMindFactory(ChatClientRegistry chatClientRegistry,
                           SseService sseService,
                           AgentMapper agentMapper,
                           AgentConverter agentConverter,
                           KnowledgeBaseMapper knowledgeBaseMapper,
                           KnowledgeBaseConverter knowledgeBaseConverter,
                           ToolFacadeService toolFacadeService,
                           ChatMessageFacadeService chatMessageFacadeService,
                           ChatMessageConverter chatMessageConverter) {

        this.chatClientRegistry = chatClientRegistry;
        this.sseService = sseService;
        this.agentMapper = agentMapper;
        this.agentConverter = agentConverter;
        this.knowledgeBaseMapper = knowledgeBaseMapper;
        this.knowledgeBaseConverter = knowledgeBaseConverter;
        this.toolFacadeService = toolFacadeService;
        this.chatMessageFacadeService = chatMessageFacadeService;
        this.chatMessageConverter = chatMessageConverter;
    }

    private Agent loadAgent(String agentId) {
        return agentMapper.selectById(agentId);
    }

    /**
     * 将数据库中存储的记忆转换为List<Message>类型
     */
    private List<Message> loadMemory(String chatSessionId) {
        int messageLength = agentConfig.getChatOptions().getMessageLength();
        List<ChatMessageDTO> chatMessages = chatMessageFacadeService.
                getChatMessagesBySessionIdRecently(chatSessionId, messageLength);
        List<Message> memory = new ArrayList<>();
        for (ChatMessageDTO chatMessageDTO : chatMessages) {
            switch (chatMessageDTO.getRole()) {
                case SYSTEM:
                    if (!StringUtils.hasLength(chatMessageDTO.getContent())) continue;
                    memory.add(0, new SystemMessage(chatMessageDTO.getContent()));
                    break;
                case USER:
                    if (!StringUtils.hasLength(chatMessageDTO.getContent())) continue;
                    memory.add(new UserMessage(chatMessageDTO.getContent()));
                    break;
                case ASSISTANT:
                    memory.add(AssistantMessage.builder()
                            .content(chatMessageDTO.getContent())
                            .toolCalls(chatMessageDTO.getMetadata()
                                    .getToolCalls())
                            .build());
                    break;
                case TOOL:
                    memory.add(ToolResponseMessage.builder()
                            .responses(List.of(chatMessageDTO.getMetadata().getToolResponse()))
                            .build());
                    break;
                default:
                    log.error("不支持的 Message 类型: {}, content = {}",
                            chatMessageDTO.getRole().getRole(),
                            chatMessageDTO.getContent()
                    );
                    throw new IllegalStateException("不支持的 Message 类型");
            }
        }
        return memory;
    }

    /**
     * 将Agent实体转换为AgentDTO
     * @param agent Agent实体
     * @return AgentDTO
     */
    private AgentDTO toAgentConfig(Agent agent) {
        try {
            agentConfig = agentConverter.toDTO(agent);
            return agentConfig;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("解析 Agent 配置失败", e);
        }
    }

    /**
     * 解析运行时知识库
     * @param agentConfig 运行时Agent配置
     * @return 运行时知识库列表
     */
    private List<KnowledgeBaseDTO> resolveRuntimeKnowledgeBases(AgentDTO agentConfig) {
        List<String> allowedKbIds = agentConfig.getAllowedKbs();
        if (allowedKbIds == null || allowedKbIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<KnowledgeBase> knowledgeBases = knowledgeBaseMapper.selectByIdBatch(allowedKbIds);
        if (knowledgeBases.isEmpty()) {
            return Collections.emptyList();
        }
        List<KnowledgeBaseDTO> kbDTOs = new ArrayList<>();
        try {
            for (KnowledgeBase knowledgeBase : knowledgeBases) {
                KnowledgeBaseDTO kbDTO = knowledgeBaseConverter.toDTO(knowledgeBase);
                kbDTOs.add(kbDTO);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return kbDTOs;
    }

    /**
     * 解析运行时工具
     * @param agentConfig 运行时Agent配置
     * @return 运行时工具列表
     */
    private List<Tool> resolveRuntimeTools(AgentDTO agentConfig) {
        List<Tool> runtimeTools = new ArrayList<>(toolFacadeService.getFixedTools());

        // 可选工具
        List<String> allowedToolNames = agentConfig.getAllowedTools();
        if (allowedToolNames == null || allowedToolNames.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Tool> optionalToolMap = toolFacadeService.getOptionalTools()
                .stream()
                .collect(Collectors.toMap(Tool::getName, Function.identity()));

        for (String toolName : allowedToolNames) {
            Tool tool = optionalToolMap.get(toolName);
            if (tool != null) {
                runtimeTools.add(tool);
            }
        }
        return runtimeTools;
    }

    /**
     * 构建工具回调, 作用是将工具对象转换为工具回调对象
     * @param runtimeTools 运行时工具列表
     * @return 工具回调列表
     */
    private List<ToolCallback> buildToolCallbacks(List<Tool> runtimeTools) {
        List<ToolCallback> callbacks = new ArrayList<>();
        for (Tool tool : runtimeTools) {
            Object target = resolveToolTarget(tool);
            ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                    .toolObjects(target)
                    .build()
                    .getToolCallbacks();

            callbacks.addAll(Arrays.asList(toolCallbacks));
        }
        return callbacks;
    }

    private Object resolveToolTarget(Tool tool) {
        try {
            return AopUtils.isAopProxy(tool)
                    ? AopUtils.getTargetClass(tool)
                    : tool;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "解析工具目标对象失败: " + tool.getName(), e);
        }
    }

    public ChatMind buildAgentRuntime(
            Agent agent,
            List<Message> memory,
            List<KnowledgeBaseDTO> knowledgeBases,
            List<ToolCallback> toolCallbacks,
            String chatSessionId
    ) {
        ChatClient chatClient = chatClientRegistry.getChatClient(agent.getModel());
        if (Objects.isNull(chatClient)) {
            throw new IllegalStateException("未找到对应的 ChatClient: " + agent.getModel());
        }

        return new ChatMind(
                agent.getId(),
                agent.getName(),
                agent.getDescription(),
                agent.getSystemPrompt(),
                chatClient,
                agentConfig.getChatOptions().getMessageLength(),
                memory,
                toolCallbacks,
                knowledgeBases,
                chatSessionId,
                sseService,
                chatMessageFacadeService,
                chatMessageConverter
        );
    }

    /**
     * 创建一个 JChatMind 实例
     */
    public ChatMind create(String agentId, String chatSessionId) {
        Agent agent = loadAgent(agentId);
        AgentDTO agentConfig = toAgentConfig(agent);
        List<Message> memory = loadMemory(chatSessionId);

        // 解析 agent 的支持的知识库
        List<KnowledgeBaseDTO> knowledgeBases = resolveRuntimeKnowledgeBases(agentConfig);
        // 解析 agent 支持的工具调用
        List<Tool> runtimeTools = resolveRuntimeTools(agentConfig);
        // 将工具调用转换成 ToolCallback 的形式
        List<ToolCallback> toolCallbacks = buildToolCallbacks(runtimeTools);

        return buildAgentRuntime(
                agent,
                memory,
                knowledgeBases,
                toolCallbacks,
                chatSessionId
        );
    }

}
