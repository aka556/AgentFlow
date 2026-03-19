package org.xiaoyu.xchatmind.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xiaoyu.xchatmind.model.common.ApiResponse;
import org.xiaoyu.xchatmind.model.request.CreateDocumentRequest;
import org.xiaoyu.xchatmind.model.request.UpdateDocumentRequest;
import org.xiaoyu.xchatmind.model.response.CreateDocumentResponse;
import org.xiaoyu.xchatmind.model.response.GetDocumentResponse;
import org.xiaoyu.xchatmind.service.DocumentFacadeService;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class DocumentController {
    private final DocumentFacadeService documentFacadeService;

    @Operation(summary = "查询全部文档")
    @GetMapping("/documents")
    public ApiResponse<GetDocumentResponse> getDocuments() {
        return ApiResponse.success(documentFacadeService.getDocuments());
    }

    @Operation(summary = "根据文档ID获取文档")
    @GetMapping("/documents/kb/{kbId}")
    public ApiResponse<GetDocumentResponse> getDocumentByKbId(@PathVariable String kbId) {
        return ApiResponse.success(documentFacadeService.getDocumentByKbId(kbId));
    }

    @Operation(summary = "创建文档")
    @PostMapping("/documents")
    public ApiResponse<CreateDocumentResponse> createDocument(@RequestBody CreateDocumentRequest request) {
        return ApiResponse.success(documentFacadeService.createDocument(request));
    }

    @Operation(summary = "上传文档并创建记录")
    @PostMapping("/documents/upload")
    public ApiResponse<CreateDocumentResponse> uploadDocument(
            @RequestParam("kbId") String kbId,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(documentFacadeService.uploadDocument(kbId, file));
    }

    @Operation(summary = "删除文档")
    @DeleteMapping("/documents/{documentId}")
    public ApiResponse<Void> deleteDocument(@PathVariable String documentId) {
        documentFacadeService.deleteDocument(documentId);
        return ApiResponse.success();
    }

    @Operation(summary = "更新文档")
    @PatchMapping("/documents/{documentId}")
    public ApiResponse<Void> updateDocument(@PathVariable String documentId, @RequestBody UpdateDocumentRequest request) {
        documentFacadeService.updateDocument(documentId, request);
        return ApiResponse.success();
    }
}
