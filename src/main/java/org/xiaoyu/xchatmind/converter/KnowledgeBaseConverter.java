package org.xiaoyu.xchatmind.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.xiaoyu.xchatmind.model.dto.KnowledgeBaseDTO;
import org.xiaoyu.xchatmind.model.entity.KnowledgeBase;
import org.xiaoyu.xchatmind.model.request.CreateKnowledgeBaseRequest;
import org.xiaoyu.xchatmind.model.request.UpdateKnowledgeBaseRequest;
import org.xiaoyu.xchatmind.model.vo.KnowledgeBaseVO;

@Component
@AllArgsConstructor
public class KnowledgeBaseConverter {
    private final ObjectMapper objectMapper;

    public KnowledgeBase toEntity(KnowledgeBaseDTO knowledgeBaseDTO) throws JsonProcessingException {
        Assert.notNull(knowledgeBaseDTO, "KnowledgeBaseDTO cannot be null");

        return KnowledgeBase.builder()
                .id(knowledgeBaseDTO.getId())
                .name(knowledgeBaseDTO.getName())
                .description(knowledgeBaseDTO.getDescription())
                .metadata(knowledgeBaseDTO.getMetadata() != null
                        ? objectMapper.writeValueAsString(knowledgeBaseDTO.getMetadata())
                        : null)
                .createdAt(knowledgeBaseDTO.getCreatedAt())
                .updatedAt(knowledgeBaseDTO.getUpdatedAt())
                .build();
    }

    public KnowledgeBaseDTO toDTO(KnowledgeBase knowledgeBase) throws JsonProcessingException {
        Assert.notNull(knowledgeBase, "KnowledgeBase cannot be null");

        return KnowledgeBaseDTO.builder()
                .id(knowledgeBase.getId())
                .name(knowledgeBase.getName())
                .description(knowledgeBase.getDescription())
                .metadata(knowledgeBase.getMetadata() != null
                        ? objectMapper.readValue(knowledgeBase.getMetadata(), KnowledgeBaseDTO.MetaData.class)
                        : null)
                .createdAt(knowledgeBase.getCreatedAt())
                .updatedAt(knowledgeBase.getUpdatedAt())
                .build();
    }

    public KnowledgeBaseVO toVO(KnowledgeBaseDTO dto) {
        return KnowledgeBaseVO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public KnowledgeBaseVO toVO(KnowledgeBase knowledgeBase) throws JsonProcessingException {
        return toVO(toDTO(knowledgeBase));
    }

    public KnowledgeBaseDTO toDTO(CreateKnowledgeBaseRequest request) {
        Assert.notNull(request, "CreateKnowledgeBaseRequest cannot be null");

        return KnowledgeBaseDTO.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public void updateDTOFromRequest(KnowledgeBaseDTO dto, UpdateKnowledgeBaseRequest request) {
        Assert.notNull(dto, "KnowledgeBaseDTO cannot be null");
        Assert.notNull(request, "UpdateKnowledgeBaseRequest cannot be null");

        if (request.getName() != null) {
            dto.setName(request.getName());
        }
        if (request.getDescription() != null) {
            dto.setDescription(request.getDescription());
        }
    }
}
