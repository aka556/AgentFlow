package org.xiaoyu.xchatmind.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xiaoyu.xchatmind.model.common.ApiResponse;
import org.xiaoyu.xchatmind.model.request.CreateKnowledgeBaseRequest;
import org.xiaoyu.xchatmind.model.request.UpdateKnowledgeBaseRequest;
import org.xiaoyu.xchatmind.model.response.CreateKnowledgeBaseResponse;
import org.xiaoyu.xchatmind.model.response.GetKnowledgeBaseResponse;
import org.xiaoyu.xchatmind.service.KnowledgeBaseFacadeService;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class KnowledgeBaseController {
    private final KnowledgeBaseFacadeService knowledgeBaseFacadeService;

    @Operation(summary = "查询知识库列表")
    @GetMapping("/knowledge-bases")
    public ApiResponse<GetKnowledgeBaseResponse> getKnowledgeBases() {
        return ApiResponse.success(knowledgeBaseFacadeService.getKnowledgeBases());
    }

    @Operation(summary = "创建知识库")
    @PostMapping("/knowledge-bases")
    public ApiResponse<CreateKnowledgeBaseResponse> createKnowledgeBase(@RequestBody CreateKnowledgeBaseRequest request) {
        return ApiResponse.success(knowledgeBaseFacadeService.createKnowledgeBase(request));
    }

    @Operation(summary = "删除知识库")
    @DeleteMapping("/knowledge-bases/{knowledgeBaseId}")
    public ApiResponse<Void> deleteKnowledgeBase(@PathVariable String knowledgeBaseId) {
        knowledgeBaseFacadeService.deleteKnowledgeBase(knowledgeBaseId);
        return ApiResponse.success();
    }

    @Operation(summary = "更新知识库")
    @PatchMapping("/knowledge-bases/{knowledgeBaseId}")
    public ApiResponse<Void> updateKnowledgeBase(@PathVariable String knowledgeBaseId, @RequestBody UpdateKnowledgeBaseRequest request) {
        knowledgeBaseFacadeService.updateKnowledgeBase(knowledgeBaseId, request);
        return ApiResponse.success();
    }
}
