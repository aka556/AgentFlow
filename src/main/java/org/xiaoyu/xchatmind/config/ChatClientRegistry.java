package org.xiaoyu.xchatmind.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author xiaoyu
 * @desciption 聊天客户端注册中心
 */
@Component
public class ChatClientRegistry {
    private final Map<String, ChatClient> chatClients;

    public ChatClientRegistry(Map<String, ChatClient> chatClients) {
        this.chatClients = chatClients;
    }

    public ChatClient getChatClient(String modelName) {
        return chatClients.get(modelName);
    }
}
