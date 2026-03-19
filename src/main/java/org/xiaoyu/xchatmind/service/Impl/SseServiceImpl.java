package org.xiaoyu.xchatmind.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.xiaoyu.xchatmind.message.SseMessage;
import org.xiaoyu.xchatmind.service.SseService;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@AllArgsConstructor
public class SseServiceImpl implements SseService {
    private final ConcurrentMap<String, SseEmitter> clients = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Override
    public void send(String chatSessionId, SseMessage sseMessage) {
        SseEmitter sseEmitter = clients.get(chatSessionId);
        if (sseEmitter != null) {
            try {
                // 将消息转换为字符串
                String sseMessageStr = objectMapper.writeValueAsString(sseMessage);
                sseEmitter.send(SseEmitter.event()
                        .name("message")
                        .data(sseMessageStr));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("No client found for chatSessionId: " + chatSessionId);
        }
    }

    @Override
    public SseEmitter connect(String chatSessionId) {
        SseEmitter sseEmitter = new SseEmitter(30 * 60 * 1000L);
        clients.put(chatSessionId, sseEmitter);

        try {
            sseEmitter.send(SseEmitter.event()
                    .name("init")
                    .data("connected"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sseEmitter.onCompletion(() -> {
            clients.remove(chatSessionId);
        });

        sseEmitter.onTimeout(() -> clients.remove(chatSessionId));
        sseEmitter.onError((error) -> clients.remove(chatSessionId));

        return sseEmitter;
    }
}
