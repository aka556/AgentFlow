package org.xiaoyu.xchatmind.agent.tools;

import org.springframework.stereotype.Component;
import org.xiaoyu.xchatmind.service.RagService;

import java.util.List;

@Component
public class KnowledgeTools implements Tool {
    private final RagService ragService;

    public KnowledgeTools(RagService ragService) {
        this.ragService = ragService;
    }

    @Override
    public String getName() {
        return "knowledgeTool";
    }

    @Override
    public String getDescription() {
        return "用于从知识库执行语义检索（RAG）。输入知识库 ID 和查询文本，返回与查询最相关的内容片段。";
    }

    @Override
    public ToolType getType() {
        return ToolType.FIXED;
    }

    @org.springframework.ai.tool.annotation.Tool(
            name = "knowledgeQuery",
            description = "从知识库执行语义检索。参数：kbsId - 知识库 ID，query - 查询文本。返回与查询最相关的内容片段。"
    )
    public String knowledgeQuery(String kbsId, String query) {
        List<String> strings = ragService.similaritySearch(kbsId, query);
        return String.join("\n", strings);
    }
}
