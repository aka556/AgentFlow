package org.xiaoyu.xchatmind.service;

import org.xiaoyu.xchatmind.model.request.CreateKnowledgeBaseRequest;
import org.xiaoyu.xchatmind.model.request.UpdateKnowledgeBaseRequest;
import org.xiaoyu.xchatmind.model.response.CreateKnowledgeBaseResponse;
import org.xiaoyu.xchatmind.model.response.GetKnowledgeBaseResponse;

public interface KnowledgeBaseFacadeService {
    GetKnowledgeBaseResponse getKnowledgeBases();

    CreateKnowledgeBaseResponse createKnowledgeBase(CreateKnowledgeBaseRequest request);

    void deleteKnowledgeBase(String knowledgeBaseId);

    void updateKnowledgeBase(String knowledgeBaseId, UpdateKnowledgeBaseRequest request);
}
