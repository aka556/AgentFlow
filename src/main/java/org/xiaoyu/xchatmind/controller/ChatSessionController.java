package org.xiaoyu.xchatmind.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xiaoyu.xchatmind.model.common.ApiResponse;
import org.xiaoyu.xchatmind.model.request.CreateChatSessionRequest;
import org.xiaoyu.xchatmind.model.request.UpdateChatSessionRequest;
import org.xiaoyu.xchatmind.model.response.CreateChatSessionResponse;
import org.xiaoyu.xchatmind.model.response.GetChatSessionResponse;
import org.xiaoyu.xchatmind.model.response.GetChatSessionsResponse;
import org.xiaoyu.xchatmind.service.ChatSessionFacadeService;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ChatSessionController {
    private final ChatSessionFacadeService chatSessionFacadeService;

    @Operation(summary = "查询所有聊天会话")
    @GetMapping("/chat-sessions")
    public ApiResponse<GetChatSessionsResponse> getChatSessions() {
        return ApiResponse.success(chatSessionFacadeService.getChatSessions());
    }

    @Operation(summary = "查询单个聊天会话")
    @GetMapping("/chat-sessions/{chatSessionId}")
    public ApiResponse<GetChatSessionResponse> getChatSession(@PathVariable String chatSessionId) {
        return ApiResponse.success(chatSessionFacadeService.getChatSession(chatSessionId));
    }

    @Operation(summary = "根据agentId查询聊天会话")
    @GetMapping("/chat-sessions/agent/{agentId}")
    public ApiResponse<GetChatSessionsResponse> getChatSessionsByAgentId(@PathVariable String agentId) {
        return ApiResponse.success(chatSessionFacadeService.getChatSessionsByAgentId(agentId));
    }

    @Operation(summary = "创建聊天会话")
    @PostMapping("/chat-sessions")
    public ApiResponse<CreateChatSessionResponse> createChatSession(@RequestBody CreateChatSessionRequest request) {
        return ApiResponse.success(chatSessionFacadeService.createChatSession(request));
    }

    @Operation(summary = "删除聊天会话")
    @DeleteMapping("/chat-sessions/{chatSessionId}")
    public ApiResponse<Void> deleteChatSession(@PathVariable String chatSessionId) {
        chatSessionFacadeService.deleteChatSession(chatSessionId);
        return ApiResponse.success();
    }

    @Operation(summary = "更新聊天会话")
    @PatchMapping("/chat-sessions/{chatSessionId}")
    public ApiResponse<Void> updateChatSession(@PathVariable String chatSessionId, @RequestBody UpdateChatSessionRequest request) {
        chatSessionFacadeService.updateChatSession(chatSessionId, request);
        return ApiResponse.success();
    }
}
