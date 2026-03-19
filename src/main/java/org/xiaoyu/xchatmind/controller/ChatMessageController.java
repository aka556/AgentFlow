package org.xiaoyu.xchatmind.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xiaoyu.xchatmind.model.common.ApiResponse;
import org.xiaoyu.xchatmind.model.request.CreateChatMessageRequest;
import org.xiaoyu.xchatmind.model.request.UpdateChatMessageRequest;
import org.xiaoyu.xchatmind.model.response.CreateChatMessageResponse;
import org.xiaoyu.xchatmind.model.response.GetChatMessageResponse;
import org.xiaoyu.xchatmind.service.ChatMessageFacadeService;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ChatMessageController {
    private final ChatMessageFacadeService chatMessageFacadeService;

    @Operation(summary = "根据会话ID获取消息列表")
    @GetMapping("/chat-messages/session/{sessionId}")
    public ApiResponse<GetChatMessageResponse> getChatMessagesBySessionId(@PathVariable String sessionId) {
        return ApiResponse.success(chatMessageFacadeService.getChatMessageBySessionId(sessionId));
    }

    @Operation(summary = "创建聊天消息")
    @PostMapping("/chat-messages")
    public ApiResponse<CreateChatMessageResponse> createChatMessage(@RequestBody CreateChatMessageRequest request) {
        return ApiResponse.success(chatMessageFacadeService.createChatMessage(request));
    }

    @Operation(summary = "删除聊天信息")
    @DeleteMapping("/chat-messages/{chatMessageId}")
    public ApiResponse<Void> deleteChatMessage(@PathVariable String chatMessageId) {
        chatMessageFacadeService.deleteChatMessage(chatMessageId);
        return ApiResponse.success();
    }

    @Operation(summary = "更新聊天信息")
    @PatchMapping("/chat-messages/{chatMessageId}")
    public ApiResponse<Void> updateChatMessage(@PathVariable String chatMessageId, @RequestBody UpdateChatMessageRequest request) {
        chatMessageFacadeService.updateChatMessage(chatMessageId, request);
        return ApiResponse.success();
    }
}
