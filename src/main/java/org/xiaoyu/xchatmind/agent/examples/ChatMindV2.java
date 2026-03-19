package org.xiaoyu.xchatmind.agent.examples;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.Assert;
import org.xiaoyu.xchatmind.agent.AgentState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * @author xiaoyu
 * @desciption pro version
 * - 增加工具调用功能
 * -- 继承 origin version
 * -- 支持工具调用
 * -- 实现 think-execute循环
 * -- 自动执行工具并调用
 */
@Slf4j
public class ChatMindV2 extends ChatMindV1 {

    // 可用工具列表
    protected List<ToolCallback> availableTools;
    // 统一执工具调用管理器
    protected ToolCallingManager toolCallingManager;
    // chatOptions
    protected ChatOptions chatOptions;
    // 记录最后一次聊天响应
    protected ChatResponse lastChatResponse;

    // 最大循环次数
    private static final Integer MAX_STEPS = 20;

    public ChatMindV2() {super();}

    public ChatMindV2(String name,
                      String description,
                      String systemPrompt,
                      ChatClient chatClient,
                      Integer maxMessages,
                      String sessionId,
                      List<ToolCallback> availableTools) {
        super(name, description, systemPrompt, chatClient, maxMessages, sessionId);
        this.availableTools = availableTools;

        // 关闭自动调用执行，改为手动控制
        this.chatOptions = DefaultToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();

        this.toolCallingManager = ToolCallingManager.builder().build();
    }

    /**
     * 打印工具调用信息
     */
    protected void printToolCalls(List<AssistantMessage.ToolCall> toolCalls) {
        if (toolCalls != null && !toolCalls.isEmpty()) {
            log.info("\n\n[ToolCalling] 无工具调用");
            return;
        }

        String logMessage = IntStream.range(0, toolCalls.size())
                .mapToObj(i -> {
                    AssistantMessage.ToolCall call = toolCalls.get(i);
                    return String.format(
                            "[ToolCalling #%d]\n- name      : %s\n- arguments : %s",
                            i + 1,
                            call.name(),
                            call.arguments()
                    );
                })
                .collect(Collectors.joining("\n\n"));
        log.info("\n\n========== Tool Calling ==========\n{}\n=================================\n", logMessage);
    }

    /**
     * think -> execute: 思考并决定是否需要调用工具
     */
    protected boolean think() {
        String thinkPrompt = """
                现在你是一个智能的「决策模块」。
                请根据当前对话上下文，决定下一步的动作。
                如果需要调用工具来完成任务，请调用相应的工具。
                """;

        // 构建系统提示词
        Prompt prompt = Prompt.builder()
                .chatOptions(chatOptions)
                .messages(chatMemory.get(sessionId))
                .build();


        // 调用LLM, 传入工具回调
        this.lastChatResponse = chatClient
                .prompt(prompt)
                .system(thinkPrompt)
                .toolCallbacks(availableTools != null ? availableTools.toArray(new ToolCallback[0]) : new ToolCallback[0])
                .call()
                .chatClientResponse()
                .chatResponse();

        Assert.notNull(lastChatResponse, "Last chat response must not be null");

        AssistantMessage output = this.lastChatResponse.getResult().getOutput();


        List<AssistantMessage.ToolCall> toolCalls = output.getToolCalls();

        // 打印工具调用信息
        printToolCalls(toolCalls);

        // 如果没有工具调用，将 AI 回复添加到记忆
        // 如果有工具调用，则在 execute() 中统一处理，避免出现未完成的工具调用
        if (toolCalls.isEmpty()) {
            chatMemory.add(sessionId, output);
        }

        // 如果工具调用不为空，则进入执行阶段
        return !toolCalls.isEmpty();
    }

    /**
     * Execute 阶段：执行工具调用
     */
    protected void execute() {
        Assert.notNull(lastChatResponse, "Last chat response must not be null");

        if (!this.lastChatResponse.hasToolCalls()) {
            return;
        }

        // 构建提示词， 包含当前的对话历史
        // 当前的chatMemory没有包含工具调用的AssistantMessage
        Prompt prompt = Prompt.builder()
                .chatOptions(chatOptions)
                .messages(chatMemory.get(sessionId))
                .build();

        // 执行工具调用
        // ToolCallingManager.executeToolCalls() 会：
        // 1. 从 prompt 中获取当前的对话历史
        // 2. 从 lastChatResponse 中获取带有 tool_calls 的 AssistantMessage，并添加到对话历史
        // 3. 执行工具调用
        // 4. 添加 ToolResponseMessage 到对话历史
        // toolExecutionResult.conversationHistory() 会包含完整的对话历史：
        // - 之前的对话历史
        // - 带有 tool_calls 的 AssistantMessage
        // - ToolResponseMessage（工具调用结果）
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, this.lastChatResponse);

        // 更新记忆
        chatMemory.clear(sessionId);
        chatMemory.add(sessionId, toolExecutionResult.conversationHistory());

        // 获取工具响应信息
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) toolExecutionResult
                .conversationHistory()
                .get(toolExecutionResult.conversationHistory().size() - 1);

        // 打印工具调用结果
        String result = toolResponseMessage.getResponses()
                .stream()
                .map(resp -> "工具 " + resp.name() + " 的返回结果为：" + resp.responseData())
                .collect(Collectors.joining("\n"));


        // 检查是否有终止工具调用
        if (toolResponseMessage.getResponses()
                .stream()
                .anyMatch(resp -> resp.name().equals("terminate"))) {
            this.agentState = AgentState.FINISHED;
            log.info("\n\n[ToolCalling] 工具调用结束：{}", result);
        }
    }

    /**
     * 单个步骤：think -> execute（如果需要）
     */
    protected void step() {
        if (think()) {
            // 有工具调用，执行工具
            execute();
        } else {
            // 没有工具调用，直接结束
            agentState = AgentState.FINISHED;
        }
    }

    /**
     * 运行 Agent：处理用户输入，执行 think-execute 循环
     */
    @Override
    public String chat(String userInput) {
        Assert.notNull(userInput, "userInput must not be null");

        if (agentState != AgentState.IDLE) {
            throw new IllegalStateException("agentState is not IDLE, 当前状态: " + agentState);
        }

        try {
            agentState = AgentState.THINKING;

            UserMessage userMessage = new UserMessage(userInput);
            chatMemory.add(sessionId, userMessage);

            // 执行 think-execute 循环
            for (int i = 0; i < MAX_STEPS && agentState != AgentState.FINISHED; i++) {
                step();
                if (i >= MAX_STEPS - 1) {
                    agentState = AgentState.FINISHED;
                    log.info("达到运行最大步数， 停止Agent");
                }
            }

            // 获取最后的AI回复信息
            List<Message> history = chatMemory.get(sessionId);
            String aiResponse = "";
            for (int i = history.size() - 1; i >= 0; i--) {
                Message message = history.get(i);
                if (message instanceof AssistantMessage) {
                    aiResponse = ((AssistantMessage) message).getText();
                    break;
                }
            }

            agentState = AgentState.FINISHED;
            return aiResponse;
        } catch (Exception e) {
            agentState = AgentState.ERROR;
            log.error("聊天过程中出现错误", e);
            throw new RuntimeException("聊天过程中出现错误", e);
        } finally {
            // 重置状态
            agentState = AgentState.IDLE;
        }
    }
}
