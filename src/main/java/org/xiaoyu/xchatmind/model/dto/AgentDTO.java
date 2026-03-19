package org.xiaoyu.xchatmind.model.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AgentDTO {
    private String id;

    private String name;

    private String description;

    private String systemPrompt;

    private ModelType model;

    private List<String> allowedTools;

    private List<String> allowedKbs;

    private ChatOptions chatOptions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Getter
    @AllArgsConstructor
    public enum ModelType {
        DEEPSEEK_CHAT("deepseek-chat"),
        GLM_4_7("glm-4.7"),
        GPT_5("gpt-5"),
        CLAUDE_SONNET("claude-3-5-sonnet-20241022");

        @JsonValue
        private final String modelName;

        public static ModelType fromModelName(String modelName) {
            for (ModelType modelType : ModelType.values()) {
                if (modelType.modelName.equals(modelName)) {
                    return modelType;
                }
            }
            throw new IllegalArgumentException("Invalid model name: " + modelName);
        }
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class ChatOptions {
        private Double temperature;
        private Double topP;
        private Integer messageLength; // 聊天消息窗口长度

        private static final Double DEFAULT_TEMPERATURE = 0.7;
        private static final Double DEFAULT_TOP_P = 1.0;
        private static final Integer DEFAULT_MESSAGE_LENGTH = 10;

        public static ChatOptions defaultChatOptions() {
            return ChatOptions.builder()
                    .temperature(DEFAULT_TEMPERATURE)
                    .topP(DEFAULT_TOP_P)
                    .messageLength(DEFAULT_MESSAGE_LENGTH)
                    .build();
        }
    }
}
