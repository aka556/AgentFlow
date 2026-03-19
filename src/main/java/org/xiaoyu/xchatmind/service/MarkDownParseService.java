package org.xiaoyu.xchatmind.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.InputStream;
import java.util.List;

public interface MarkDownParseService {

    List<MarkdownSection> parseMarkdown(InputStream inputStream);

    /**
     * Markdown 章节数据类
     */
    @Data
    @AllArgsConstructor
    @ToString
    class MarkdownSection {
        private String title;
        private String content;
    }
}
