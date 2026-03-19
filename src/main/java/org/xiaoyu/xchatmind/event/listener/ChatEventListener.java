package org.xiaoyu.xchatmind.event.listener;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.xiaoyu.xchatmind.agent.ChatMind;
import org.xiaoyu.xchatmind.agent.ChatMindFactory;
import org.xiaoyu.xchatmind.event.ChatEvent;

@Component
@AllArgsConstructor
public class ChatEventListener {
    private final ChatMindFactory chatMindFactory;

    @Async
    @EventListener
    public void handle(ChatEvent event) {
        // 创建Agent实例处理聊天事件
        ChatMind chatMind = chatMindFactory.create(event.getAgentId(), event.getSessionId());
        chatMind.run();
    }
}
