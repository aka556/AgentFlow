package org.xiaoyu.xchatmind.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xiaoyu.xchatmind.model.entity.Document;

import java.util.List;

@Mapper
public interface DocumentMapper {

    List<Document> selectAll();

    List<Document> selectByKbId(String kbId);

    int insert(Document document);

    int updateById(Document updatedDocument);

    Document selectById(String id);

    int deleteById(String documentId);
}
