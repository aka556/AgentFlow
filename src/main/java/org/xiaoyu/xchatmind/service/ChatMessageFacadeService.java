package org.xiaoyu.xchatmind.service;

import org.xiaoyu.xchatmind.model.common.ApiResponse;
import org.xiaoyu.xchatmind.model.dto.ChatMessageDTO;
import org.xiaoyu.xchatmind.model.request.CreateChatMessageRequest;
import org.xiaoyu.xchatmind.model.request.UpdateChatMessageRequest;
import org.xiaoyu.xchatmind.model.response.CreateChatMessageResponse;
import org.xiaoyu.xchatmind.model.response.GetChatMessageResponse;

import java.util.List;

public interface ChatMessageFacadeService {
    CreateChatMessageResponse createChatMessage(ChatMessageDTO chatMessageDTO);

    CreateChatMessageResponse createChatMessage(CreateChatMessageRequest request);

    List<ChatMessageDTO> getChatMessagesBySessionIdRecently(String chatSessionId, int limit);

    GetChatMessageResponse getChatMessageBySessionId(String sessionId);

    void deleteChatMessage(String chatMessageId);

    void updateChatMessage(String chatMessageId, UpdateChatMessageRequest request);
}
