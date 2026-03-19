package org.xiaoyu.xchatmind.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.xiaoyu.xchatmind.converter.ChatMessageConverter;
import org.xiaoyu.xchatmind.event.ChatEvent;
import org.xiaoyu.xchatmind.exception.BizException;
import org.xiaoyu.xchatmind.mapper.ChatMessageMapper;
import org.xiaoyu.xchatmind.model.dto.ChatMessageDTO;
import org.xiaoyu.xchatmind.model.entity.ChatMessage;
import org.xiaoyu.xchatmind.model.request.CreateChatMessageRequest;
import org.xiaoyu.xchatmind.model.request.UpdateChatMessageRequest;
import org.xiaoyu.xchatmind.model.response.CreateChatMessageResponse;
import org.xiaoyu.xchatmind.model.response.GetChatMessageResponse;
import org.xiaoyu.xchatmind.model.vo.ChatMessageVO;
import org.xiaoyu.xchatmind.service.ChatMessageFacadeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatMessageFacadeServiceImpl implements ChatMessageFacadeService {
    private final ChatMessageMapper chatMessageMapper;
    private final ChatMessageConverter chatMessageConverter;
    private final ApplicationEventPublisher publisher;

    @Override
    public CreateChatMessageResponse createChatMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessage chatMessage = doCreateChatMessage(chatMessageDTO);
        return CreateChatMessageResponse.builder()
                .chatMessageId(chatMessage.getId())
                .build();
    }

    @Override
    public CreateChatMessageResponse createChatMessage(CreateChatMessageRequest request) {
        ChatMessage chatMessage = doCreateChatMessage(request);

        // 发布聊天通知事件
        publisher.publishEvent(new ChatEvent(
                request.getAgentId(),
                request.getSessionId(),
                chatMessage.getContent()
        ));

        // 返回生成的聊天消息 ID
        return CreateChatMessageResponse.builder()
                .chatMessageId(chatMessage.getId())
                .build();
    }

    @Override
    public List<ChatMessageDTO> getChatMessagesBySessionIdRecently(String chatSessionId, int limit) {
        List<ChatMessage> chatMessages = chatMessageMapper.selectBySessionIdRecently(chatSessionId, limit);
        List<ChatMessageDTO> result = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessages) {
            try {
                ChatMessageDTO dto = chatMessageConverter.toDTO(chatMessage);
                result.add(dto);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Override
    public GetChatMessageResponse getChatMessageBySessionId(String sessionId) {
        List<ChatMessage> chatMessages = chatMessageMapper.selectBySessionId(sessionId);
        List<ChatMessageVO> vos = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages) {
            try {
                ChatMessageVO vo = chatMessageConverter.toVO(chatMessage);
                vos.add(vo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return GetChatMessageResponse.builder()
                .chatMessages(vos.toArray(new ChatMessageVO[0]))
                .build();
    }

    @Override
    public void deleteChatMessage(String chatMessageId) {
        ChatMessage chatMessage = chatMessageMapper.selectById(chatMessageId);

        if (chatMessage == null) {
            throw new BizException("聊天消息不存在" + chatMessageId);
        }

        int result = chatMessageMapper.deleteById(chatMessageId);
        if (result <= 0) {
            throw new BizException("删除聊天消息失败");
        }
    }

    @Override
    public void updateChatMessage(String chatMessageId, UpdateChatMessageRequest request) {
        try {
            ChatMessage existingChatMessage = chatMessageMapper.selectById(chatMessageId);
            if (existingChatMessage == null) {
                throw new BizException("聊天消息不存在" + chatMessageId);
            }

            // 转换为 ChatMessageDTO
            ChatMessageDTO chatMessageDTO = chatMessageConverter.toDTO(existingChatMessage);

            // 更新 ChatMessageDTO
            chatMessageConverter.updateDTOFromRequest(chatMessageDTO, request);

            // 将 ChatMessageDTO 转换为 ChatMessage 实体
            ChatMessage updatedChatMessage = chatMessageConverter.toEntity(chatMessageDTO);

            // 保留原有的 ID、sessionId、role 和创建时间,更新时间为当前
            updatedChatMessage.setId(existingChatMessage.getId());
            updatedChatMessage.setSessionId(existingChatMessage.getSessionId());
            updatedChatMessage.setRole(existingChatMessage.getRole());
            updatedChatMessage.setCreatedAt(existingChatMessage.getCreatedAt());
            updatedChatMessage.setUpdatedAt(LocalDateTime.now());

            // 更新数据库
            int result = chatMessageMapper.updateById(updatedChatMessage);
            if (result <= 0) {
                throw new BizException("更新聊天消息失败");
            }
        } catch (JsonProcessingException e) {
            throw new BizException("更新聊天消息时发生序列化错误: " + e.getMessage());
        }
    }

    private ChatMessage doCreateChatMessage(CreateChatMessageRequest request) {
        // 将 CreateChatMessageRequest 转换为 ChatMessageDTO
        ChatMessageDTO chatMessageDTO = chatMessageConverter.toDTO(request);
        // 将 ChatMessageDTO 转换为 ChatMessage 实体
        return doCreateChatMessage(chatMessageDTO);
    }

    private ChatMessage doCreateChatMessage(ChatMessageDTO chatMessageDTO) {
        try {
            // 将 ChatMessageDTO 转换为 ChatMessage 实体
            ChatMessage chatMessage = chatMessageConverter.toEntity(chatMessageDTO);

            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            chatMessage.setCreatedAt(now);
            chatMessage.setUpdatedAt(now);
            // 插入数据库，ID 由数据库自动生成
            int result = chatMessageMapper.insert(chatMessage);
            if (result <= 0) {
                throw new BizException("创建聊天消息失败");
            }
            return chatMessage;
        } catch (JsonProcessingException e) {
            throw new BizException("创建聊天消息时发生序列化错误: " + e.getMessage());
        }
    }
}
