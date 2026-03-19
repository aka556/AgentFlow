package org.xiaoyu.xchatmind.model.request;

import lombok.Builder;
import lombok.Data;
import org.xiaoyu.xchatmind.model.dto.ChatMessageDTO;

@Data
@Builder
public class CreateChatMessageRequest {
    private String agentId;
    private String sessionId;
    private ChatMessageDTO.RoleType role;
    private String content;
    private ChatMessageDTO.MetaData metadata;
}
