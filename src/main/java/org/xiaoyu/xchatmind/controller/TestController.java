package org.xiaoyu.xchatmind.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiaoyu.xchatmind.service.SseService;

@RestController
@AllArgsConstructor
public class TestController {
    private final SseService sseService;

    @RequestMapping("/health")
    public String health() {
        return "ok";
    }

    @RequestMapping("/sse-test")
    public String sseTest() {
        return "ok";
    }
}
