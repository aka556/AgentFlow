package org.xiaoyu.xchatmind.service;

import java.util.List;

public interface RagService {
    List<String> similaritySearch(String kbsId, String query);

    float[] embed(String text);
}
