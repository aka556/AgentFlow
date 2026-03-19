package org.xiaoyu.xchatmind.agent.examples;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.xiaoyu.xchatmind.agent.AgentState;

import java.util.List;

/**
 * @author xiaoyu
 * @desciption original version
 * -- 集成LLM, 实现基本聊天功能
 * - 使用 ChatClient 与 LLM 交互
 * - 使用 ChatMemory 管理对话历史
 * - 支持系统提示词
 * - 简单的用户输入 -> AI 回复流程
 */
@Slf4j
public class ChatMindV1 {
    protected String name;

    protected String description;

    protected String systemPrompt;

    protected ChatClient chatClient;

    protected ChatMemory chatMemory;

    protected AgentState agentState;

    protected String sessionId;

    private static final Integer DEFAULT_MAX_MESSAGES = 20;

    public ChatMindV1() {}

    public ChatMindV1(String name,
                      String description,
                      String systemPrompt,
                      ChatClient chatClient,
                      Integer maxMessages,
                      String sessionId) {
        this.name = name;
        this.description = description;
        this.systemPrompt = systemPrompt;
        this.chatClient = chatClient;
        this.sessionId = sessionId != null ? sessionId : "default-session";
        this.agentState = AgentState.IDLE;

        // 初始化聊天记忆
        this.chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(maxMessages != null ? maxMessages : DEFAULT_MAX_MESSAGES)
                .build();

        // 系统提示词
        if (StringUtils.hasLength(systemPrompt)) {
            this.chatMemory.add(this.sessionId, new SystemMessage(systemPrompt));
        }
    }

    /**
     * 处理用户输入并返回智能体回复
     */
    public String chat(String userInput) {
        Assert.notNull(userInput, "userInput must not be null");
        if (agentState != AgentState.IDLE) {
            throw new IllegalStateException("agentState is not IDLE, 当前状态: " + agentState);
        }

        try {
            agentState = AgentState.THINKING;

            // 添加用户消息到记忆
            UserMessage userMessage = new UserMessage(userInput);
            chatMemory.add(sessionId, userMessage);

            // 构建提示词
            Prompt prompt = Prompt.builder()
                    .messages(chatMemory.get(sessionId))
                    .build();

            // 与 LLM 交互
            ChatResponse chatResponse = chatClient.prompt().call().chatResponse();

            Assert.notNull(chatResponse, "chatResponse must not be null");

            // 获取智能体回复
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            String apiResponse = assistantMessage.getText();

            // 添加智能体回复到记忆
            chatMemory.add(sessionId, assistantMessage);
            agentState = AgentState.FINISHED;

            return apiResponse;
        } catch (Exception e) {
            agentState = AgentState.ERROR;
            log.error("聊天过程中出现错误", e);
            throw new RuntimeException("聊天过程中出现错误", e);
        } finally {
            agentState = AgentState.IDLE;
        }
    }

    /**
     * 获取当前对话历史
     */
    public List<Message> getConversationHistory() {
        return chatMemory.get(sessionId);
    }

    /**
     * 重置对话历史
     */
    public void resetConversationHistory() {
        chatMemory.clear(sessionId);
        if (StringUtils.hasLength(systemPrompt)) {
            chatMemory.add(sessionId, new SystemMessage(systemPrompt));
        }
        agentState = AgentState.IDLE;
    }

    @Override
    public String toString() {
        return "ChatMindV1 {" +
                "name = " + name + ",\n" +
                "description = " + description + ",\n" +
                "systemPrompt = " + systemPrompt + "}";
    }
}
