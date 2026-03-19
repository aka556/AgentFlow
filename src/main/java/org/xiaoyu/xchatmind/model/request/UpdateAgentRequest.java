package org.xiaoyu.xchatmind.model.request;

import lombok.Data;
import org.xiaoyu.xchatmind.model.dto.AgentDTO;

import java.util.List;

@Data
public class UpdateAgentRequest {
    private String name;
    private String description;
    private String systemPrompt;
    private String model;
    private List<String> allowedTools;
    private List<String> allowedKbs;
    private AgentDTO.ChatOptions chatOptions;
}
