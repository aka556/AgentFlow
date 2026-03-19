package org.xiaoyu.xchatmind.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xiaoyu.xchatmind.model.entity.KnowledgeBase;

import java.util.List;

@Mapper
public interface KnowledgeBaseMapper {
    List<KnowledgeBase> selectByIdBatch(List<String> ids);

    List<KnowledgeBase> selectAll();

    int insert(KnowledgeBase knowledgeBase);

    KnowledgeBase selectById(String id);

    int deleteById(String id);

    int updateById(KnowledgeBase updatedKnowledgeBase);
}
