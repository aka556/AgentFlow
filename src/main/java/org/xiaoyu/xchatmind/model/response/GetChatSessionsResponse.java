package org.xiaoyu.xchatmind.model.response;

import lombok.Builder;
import lombok.Data;
import org.xiaoyu.xchatmind.model.vo.ChatSessionVO;

@Data
@Builder
public class GetChatSessionsResponse {
    private ChatSessionVO[] chatSessions;
}
