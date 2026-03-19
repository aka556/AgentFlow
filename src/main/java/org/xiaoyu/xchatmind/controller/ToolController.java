package org.xiaoyu.xchatmind.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiaoyu.xchatmind.agent.tools.Tool;
import org.xiaoyu.xchatmind.model.common.ApiResponse;
import org.xiaoyu.xchatmind.service.ToolFacadeService;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ToolController {
    private final ToolFacadeService toolFacadeService;

    @Operation(summary = "提供可选的工具列表")
    @GetMapping("/tools")
    public ApiResponse<List<Tool>> getOptionalTools() {
        return ApiResponse.success(toolFacadeService.getOptionalTools());
    }
}
