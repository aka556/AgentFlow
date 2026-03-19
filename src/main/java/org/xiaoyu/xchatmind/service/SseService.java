package org.xiaoyu.xchatmind.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.xiaoyu.xchatmind.message.SseMessage;

public interface SseService {
    void send(String chatSessionId, SseMessage sseMessage);

    SseEmitter connect(String chatSessionId);
}
