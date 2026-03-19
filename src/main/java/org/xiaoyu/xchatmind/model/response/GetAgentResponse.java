package org.xiaoyu.xchatmind.model.response;

import lombok.Builder;
import lombok.Data;
import org.xiaoyu.xchatmind.model.vo.AgentVO;

@Data
@Builder
public class GetAgentResponse {
    private AgentVO[] agents;
}
