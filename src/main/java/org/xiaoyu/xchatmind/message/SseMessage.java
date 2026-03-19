package org.xiaoyu.xchatmind.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.xiaoyu.xchatmind.model.vo.ChatMessageVO;

@Data
@Builder
@AllArgsConstructor
public class SseMessage {
    private Type type;

    private Payload payload;

    private Metadata metadata;

    @Data
    @Builder
    @AllArgsConstructor
    public static class Payload {
        private ChatMessageVO message;
        private String statusText;
        private Boolean done;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Metadata {
        private String chatMessageId;
    }

    // 自定义消息类型
    // 1. AI 生成
    // 2. AI 规划中
    // 3. AI 思考中
    // 4. AI 执行中
    // 5. AI 完成
    public enum Type {
        AI_GENERATED_CONTENT,
        AI_PLANNING,
        AI_THINKING,
        AI_EXECUTING,
        AI_DONE,
    }
}
