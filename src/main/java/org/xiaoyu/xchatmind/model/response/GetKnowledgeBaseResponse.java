package org.xiaoyu.xchatmind.model.response;

import lombok.Builder;
import lombok.Data;
import org.xiaoyu.xchatmind.model.vo.KnowledgeBaseVO;

@Data
@Builder
public class GetKnowledgeBaseResponse {
    private KnowledgeBaseVO[] knowledgeBases;
}
