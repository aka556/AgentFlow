package org.xiaoyu.xchatmind.service;

import org.springframework.web.multipart.MultipartFile;
import org.xiaoyu.xchatmind.model.request.CreateDocumentRequest;
import org.xiaoyu.xchatmind.model.request.UpdateDocumentRequest;
import org.xiaoyu.xchatmind.model.response.CreateDocumentResponse;
import org.xiaoyu.xchatmind.model.response.GetDocumentResponse;

public interface DocumentFacadeService {
    GetDocumentResponse getDocuments();

    GetDocumentResponse getDocumentByKbId(String kbId);

    CreateDocumentResponse createDocument(CreateDocumentRequest request);

    CreateDocumentResponse uploadDocument(String kbId, MultipartFile file);

    void deleteDocument(String documentId);

    void updateDocument(String documentId, UpdateDocumentRequest request);
}
