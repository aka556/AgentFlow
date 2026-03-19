package org.xiaoyu.xchatmind.model.request;

import lombok.Data;
import org.xiaoyu.xchatmind.model.dto.ChatMessageDTO;

@Data
public class UpdateChatMessageRequest {
    private String content;
    private ChatMessageDTO.MetaData metadata;
}
