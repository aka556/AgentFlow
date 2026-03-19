package org.xiaoyu.xchatmind.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.xiaoyu.xchatmind.model.vo.ChatSessionVO;

@Data
@Builder
@AllArgsConstructor
public class GetChatSessionResponse {
    private ChatSessionVO chatSession;
}
