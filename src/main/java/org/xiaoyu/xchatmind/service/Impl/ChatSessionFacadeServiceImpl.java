package org.xiaoyu.xchatmind.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xiaoyu.xchatmind.converter.ChatSessionConverter;
import org.xiaoyu.xchatmind.exception.BizException;
import org.xiaoyu.xchatmind.mapper.ChatSessionMapper;
import org.xiaoyu.xchatmind.model.dto.ChatSessionDTO;
import org.xiaoyu.xchatmind.model.entity.ChatSession;
import org.xiaoyu.xchatmind.model.request.CreateChatSessionRequest;
import org.xiaoyu.xchatmind.model.request.UpdateChatSessionRequest;
import org.xiaoyu.xchatmind.model.response.CreateChatSessionResponse;
import org.xiaoyu.xchatmind.model.response.GetChatSessionResponse;
import org.xiaoyu.xchatmind.model.response.GetChatSessionsResponse;
import org.xiaoyu.xchatmind.model.vo.ChatSessionVO;
import org.xiaoyu.xchatmind.service.ChatSessionFacadeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatSessionFacadeServiceImpl implements ChatSessionFacadeService {
    private final ChatSessionMapper chatSessionMapper;

    private final ChatSessionConverter chatSessionConverter;

    @Override
    public GetChatSessionsResponse getChatSessions() {
        List<ChatSession> chatSessions = chatSessionMapper.selectAll();
        List<ChatSessionVO> result = new ArrayList<>();

        for (ChatSession chatSession : chatSessions) {
            try {
                ChatSessionVO vo = chatSessionConverter.toVO(chatSession);
                result.add(vo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return GetChatSessionsResponse.builder()
                .chatSessions(result.toArray(new ChatSessionVO[0]))
                .build();
    }

    @Override
    public GetChatSessionResponse getChatSession(String chatSessionId) {
        ChatSession chatSession = chatSessionMapper.selectById(chatSessionId);
        if (chatSession != null) {
            try {
                ChatSessionVO vo = chatSessionConverter.toVO(chatSession);
                return GetChatSessionResponse.builder()
                        .chatSession(vo)
                        .build();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        throw new BizException("聊天会话不存在" + chatSessionId);
    }

    @Override
    public GetChatSessionsResponse getChatSessionsByAgentId(String agentId) {
        List<ChatSession> chatSessions = chatSessionMapper.selectByAgentId(agentId);
        List<ChatSessionVO> vos = new ArrayList<>();

        for (ChatSession chatSession : chatSessions) {
            try {
                ChatSessionVO vo = chatSessionConverter.toVO(chatSession);
                vos.add(vo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return GetChatSessionsResponse.builder()
                .chatSessions(vos.toArray(new ChatSessionVO[0]))
                .build();
    }

    @Override
    public CreateChatSessionResponse createChatSession(CreateChatSessionRequest request) {
        try {
            // 转换为DTO
            ChatSessionDTO chatSessionDTO = chatSessionConverter.toDTO(request);

            // 转换为实体
            ChatSession chatSession = chatSessionConverter.toEntity(chatSessionDTO);

            // 设置时间
            LocalDateTime now = LocalDateTime.now();
            chatSession.setCreatedAt(now);
            chatSession.setUpdatedAt(now);

            // 插入数据库
            int result = chatSessionMapper.insert(chatSession);
            if (result <= 0) {
                throw new BizException("创建聊天会话失败");
            }

            return CreateChatSessionResponse.builder()
                    .chatSessionId(chatSession.getId())
                    .build();
        } catch (JsonProcessingException e) {
            throw new BizException("创建聊天会话时发生序列化错误: " + e.getMessage());
        }
    }

    @Override
    public void deleteChatSession(String chatSessionId) {
        ChatSession chatSession = chatSessionMapper.selectById(chatSessionId);
        if (chatSession == null) {
            throw new BizException("聊天会话不存在" + chatSessionId);
        }

        int result = chatSessionMapper.deleteById(chatSessionId);
        if (result <= 0) {
            throw new BizException("删除聊天会话失败" + chatSessionId);
        }
    }

    @Override
    public void updateChatSession(String chatSessionId, UpdateChatSessionRequest request) {
        try {
            ChatSession existingChatSession = chatSessionMapper.selectById(chatSessionId);
            if (existingChatSession == null) {
                throw new BizException("聊天会话不存在" + chatSessionId);
            }

            // 转换为DTO
            ChatSessionDTO chatSessionDTO = chatSessionConverter.toDTO(existingChatSession);

            // 更新实体
            chatSessionConverter.updateDTOFromRequest(chatSessionDTO, request);

            // 转换为实体
            ChatSession chatSession = chatSessionConverter.toEntity(chatSessionDTO);

            // 保留原有Id, 创建时间等
            chatSession.setId(existingChatSession.getId());
            chatSession.setAgentId(existingChatSession.getAgentId());
            chatSession.setCreatedAt(existingChatSession.getCreatedAt());
            chatSession.setUpdatedAt(LocalDateTime.now());

            // 更新数据库
            int result = chatSessionMapper.updateById(chatSession);
            if (result <= 0) {
                throw new BizException("更新聊天会话失败" + chatSessionId);
            }
        } catch (JsonProcessingException e) {
            throw new BizException("更新聊天会话时发生序列化错误: " + e.getMessage());
        }
    }
}
