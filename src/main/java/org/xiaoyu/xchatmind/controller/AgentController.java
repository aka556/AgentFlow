package org.xiaoyu.xchatmind.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xiaoyu.xchatmind.model.common.ApiResponse;
import org.xiaoyu.xchatmind.model.request.CreateAgentRequest;
import org.xiaoyu.xchatmind.model.request.UpdateAgentRequest;
import org.xiaoyu.xchatmind.model.response.CreateAgentResponse;
import org.xiaoyu.xchatmind.model.response.GetAgentResponse;
import org.xiaoyu.xchatmind.service.AgentFacadeService;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AgentController {
    private final AgentFacadeService agentFacadeService;

    @Operation(description = "查询agents")
    @GetMapping("/agents")
    public ApiResponse<GetAgentResponse> getAgents() {
        return ApiResponse.success(agentFacadeService.getAgents());
    }

    @Operation(description = "创建agent")
    @PostMapping("/agents")
    public ApiResponse<CreateAgentResponse> createAgent(@RequestBody CreateAgentRequest request) {
        return ApiResponse.success(agentFacadeService.createAgent(request));
    }

    @Operation(description = "删除agent")
    @DeleteMapping("/agents/{agentId}")
    public ApiResponse<Void> deleteAgent(@PathVariable String agentId) {
        agentFacadeService.deleteAgent(agentId);
        return ApiResponse.success();
    }

    @Operation(description = "更新Agent")
    @PatchMapping("/agents/{agentId}")
    public ApiResponse<Void> updateAgent(@PathVariable String agentId, @RequestBody UpdateAgentRequest request) {
        agentFacadeService.updateAgent(agentId, request);
        return ApiResponse.success();
    }
}
