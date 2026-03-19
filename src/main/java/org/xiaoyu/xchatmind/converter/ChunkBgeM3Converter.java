package org.xiaoyu.xchatmind.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.xiaoyu.xchatmind.model.dto.ChunkBgeM3DTO;
import org.xiaoyu.xchatmind.model.entity.ChunkBgeM3;

@Component
@AllArgsConstructor
public class ChunkBgeM3Converter {
    private final ObjectMapper objectMapper;

    public ChunkBgeM3 toEntity(ChunkBgeM3DTO chunkBgeM3DTO) throws JsonProcessingException {
        Assert.notNull(chunkBgeM3DTO, "chunkBgeM3DTO must not be null");

        return ChunkBgeM3.builder()
                .id(chunkBgeM3DTO.getId())
                .kbId(chunkBgeM3DTO.getKbId())
                .docId(chunkBgeM3DTO.getDocId())
                .content(chunkBgeM3DTO.getContent())
                .metadata(chunkBgeM3DTO.getMetadata() != null
                        ? objectMapper.writeValueAsString(chunkBgeM3DTO.getMetadata())
                        : null)
                .embedding(chunkBgeM3DTO.getEmbedding())
                .createdAt(chunkBgeM3DTO.getCreatedAt())
                .updatedAt(chunkBgeM3DTO.getUpdatedAt())
                .build();
    }

    public ChunkBgeM3DTO toDTO(ChunkBgeM3 chunkBgeM3) throws JsonProcessingException {
        Assert.notNull(chunkBgeM3, "chunkBgeM3 must not be null");

        return ChunkBgeM3DTO.builder()
                .id(chunkBgeM3.getId())
                .kbId(chunkBgeM3.getKbId())
                .docId(chunkBgeM3.getDocId())
                .content(chunkBgeM3.getContent())
                .metadata(chunkBgeM3.getMetadata() != null
                        ? objectMapper.readValue(chunkBgeM3.getMetadata(), ChunkBgeM3DTO.MetaData.class)
                        : null)
                .embedding(chunkBgeM3.getEmbedding())
                .createdAt(chunkBgeM3.getCreatedAt())
                .updatedAt(chunkBgeM3.getUpdatedAt())
                .build();
    }
}
