package org.xiaoyu.xchatmind.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaoyu
 * @desciption 多模型聊天客户端配置, 集成openai、zhipuai、deepseek
 */
@Configuration
public class MultiChatClientConfig {
    @Bean("deepseek-chat")
    public ChatClient deepseekChatClient(DeepSeekChatModel deepSeekChatModel) {
        return ChatClient.create(deepSeekChatModel);
    }

    @Bean("gpt-5")
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.create(openAiChatModel);
    }

    @Bean("glm-4.7")
    public ChatClient zhipuChatClient(ZhiPuAiChatModel zhiPuAiChatModel) {
        return ChatClient.create(zhiPuAiChatModel);
    }

    @Bean("claude-3-5-sonnet-20241022")
    public ChatClient claudeChatClient(AnthropicChatModel anthropicChatModel) {
        return ChatClient.create(anthropicChatModel);
    }
}
