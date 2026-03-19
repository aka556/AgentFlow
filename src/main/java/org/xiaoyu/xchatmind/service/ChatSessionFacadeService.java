package org.xiaoyu.xchatmind.service;

import org.xiaoyu.xchatmind.model.request.CreateChatSessionRequest;
import org.xiaoyu.xchatmind.model.request.UpdateChatSessionRequest;
import org.xiaoyu.xchatmind.model.response.CreateChatSessionResponse;
import org.xiaoyu.xchatmind.model.response.GetChatSessionResponse;
import org.xiaoyu.xchatmind.model.response.GetChatSessionsResponse;

public interface ChatSessionFacadeService {
    GetChatSessionsResponse getChatSessions();

    GetChatSessionResponse getChatSession(String chatSessionId);

    GetChatSessionsResponse getChatSessionsByAgentId(String agentId);

    CreateChatSessionResponse createChatSession(CreateChatSessionRequest request);

    void deleteChatSession(String chatSessionId);

    void updateChatSession(String chatSessionId, UpdateChatSessionRequest request);
}
