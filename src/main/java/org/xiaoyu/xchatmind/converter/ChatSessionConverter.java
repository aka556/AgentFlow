package org.xiaoyu.xchatmind.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.xiaoyu.xchatmind.model.dto.ChatSessionDTO;
import org.xiaoyu.xchatmind.model.entity.ChatSession;
import org.xiaoyu.xchatmind.model.request.CreateChatSessionRequest;
import org.xiaoyu.xchatmind.model.request.UpdateChatSessionRequest;
import org.xiaoyu.xchatmind.model.vo.ChatSessionVO;

@Component
@AllArgsConstructor
public class ChatSessionConverter {
    private final ObjectMapper objectMapper;

    public ChatSession toEntity(ChatSessionDTO chatSessionDTO) throws JsonProcessingException {
        Assert.notNull(chatSessionDTO, "chatSessionDTO must not be null");

        return ChatSession.builder()
                .id(chatSessionDTO.getId())
                .agentId(chatSessionDTO.getAgentId())
                .title(chatSessionDTO.getTitle())
                .metadata(chatSessionDTO.getMetadata() != null
                     ? objectMapper.writeValueAsString(chatSessionDTO.getMetadata())
                     : null)
                .createdAt(chatSessionDTO.getCreatedAt())
                .updatedAt(chatSessionDTO.getUpdatedAt())
                .build();
    }

    public ChatSessionDTO toDTO(ChatSession chatSession) throws JsonProcessingException {
        Assert.notNull(chatSession, "chatSession must not be null");

        return ChatSessionDTO.builder()
                .id(chatSession.getId())
                .agentId(chatSession.getAgentId())
                .title(chatSession.getTitle())
                .metadata(chatSession.getMetadata() != null
                     ? objectMapper.readValue(chatSession.getMetadata(), ChatSessionDTO.MetaData.class)
                     : null)
                .createdAt(chatSession.getCreatedAt())
                .updatedAt(chatSession.getUpdatedAt())
                .build();
    }

    public ChatSessionVO toVO(ChatSessionDTO dto) {
        return ChatSessionVO.builder()
                .id(dto.getId())
                .agentId(dto.getAgentId())
                .title(dto.getTitle())
                .build();
    }

    public ChatSessionVO toVO(ChatSession chatSession) throws JsonProcessingException {
        return ChatSessionVO.builder()
                .id(chatSession.getId())
                .agentId(chatSession.getAgentId())
                .title(chatSession.getTitle())
                .build();
    }

    public ChatSessionDTO toDTO(CreateChatSessionRequest request) {
        Assert.notNull(request, "CreateChatSessionRequest must not be null");
        Assert.notNull(request.getAgentId(), "agentId must not be null");

        return ChatSessionDTO.builder()
                .agentId(request.getAgentId())
                .title(request.getTitle())
                .build();
    }

    public void updateDTOFromRequest(ChatSessionDTO dto, UpdateChatSessionRequest request) {
        Assert.notNull(dto, "ChatSessionDTO must not be null");
        Assert.notNull(request, "UpdateChatSessionRequest must not be null");

        if (request.getTitle() != null) {
            dto.setTitle(request.getTitle());
        }
    }
}
