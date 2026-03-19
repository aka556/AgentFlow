package org.xiaoyu.xchatmind.service.Impl;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.xiaoyu.xchatmind.mapper.ChunkBgeM3Mapper;
import org.xiaoyu.xchatmind.model.entity.ChunkBgeM3;
import org.xiaoyu.xchatmind.service.RagService;

import java.util.List;
import java.util.Map;

@Service
public class RagServiceImpl implements RagService {
    // 封装本地模型调用
    private final WebClient webClient;
    private final ChunkBgeM3Mapper chunkBgeM3Mapper;

    public RagServiceImpl(WebClient.Builder builder, ChunkBgeM3Mapper chunkBgeM3Mapper) {
        this.webClient = builder.baseUrl("http://localhost:11434").build();
        this.chunkBgeM3Mapper = chunkBgeM3Mapper;
    }

    @Data
    private static class EmbeddingResponse {
        private float[] embedding;
    }

    private float[] doEmbed(String text) {
        EmbeddingResponse response = webClient.post()
                .uri("/api/embeddings")
                .bodyValue(Map.of(
                        "model", "bge-m3",
                        "prompt", text
                ))
                .retrieve()
                .bodyToMono(EmbeddingResponse.class)
                .block();
        Assert.notNull(response, "Embedding response must not be null");
        return response.getEmbedding();
    }

    @Override
    public List<String> similaritySearch(String kbId, String query) {
        String queryEmbedding = toPgVector(doEmbed(query));
        List<ChunkBgeM3> chunks = chunkBgeM3Mapper.similaritySearch(kbId, queryEmbedding, 3);
        return chunks.stream().map(ChunkBgeM3::getContent).toList();
    }

    @Override
    public float[] embed(String text) {
        return doEmbed(text);
    }

    private String toPgVector(float[] v) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < v.length; i++) {
            stringBuilder.append(v[i]);
            if (i < v.length - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
