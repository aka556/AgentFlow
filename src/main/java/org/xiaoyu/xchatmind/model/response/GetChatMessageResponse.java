package org.xiaoyu.xchatmind.model.response;

import lombok.Builder;
import lombok.Data;
import org.xiaoyu.xchatmind.model.vo.ChatMessageVO;

@Data
@Builder
public class GetChatMessageResponse {
    private ChatMessageVO[] chatMessages;
}
