package org.xiaoyu.xchatmind.service;

import org.xiaoyu.xchatmind.model.request.CreateAgentRequest;
import org.xiaoyu.xchatmind.model.request.UpdateAgentRequest;
import org.xiaoyu.xchatmind.model.response.CreateAgentResponse;
import org.xiaoyu.xchatmind.model.response.GetAgentResponse;

public interface AgentFacadeService {
    GetAgentResponse getAgents();

    CreateAgentResponse createAgent(CreateAgentRequest request);

    void deleteAgent(String agentId);

    void updateAgent(String agentId, UpdateAgentRequest request);
}
