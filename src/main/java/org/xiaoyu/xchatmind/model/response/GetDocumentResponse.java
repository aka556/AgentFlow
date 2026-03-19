package org.xiaoyu.xchatmind.model.response;

import lombok.Builder;
import lombok.Data;
import org.xiaoyu.xchatmind.model.vo.ChatSessionVO;
import org.xiaoyu.xchatmind.model.vo.DocumentVO;

@Data
@Builder
public class GetDocumentResponse {
    private DocumentVO[] documents;
}
