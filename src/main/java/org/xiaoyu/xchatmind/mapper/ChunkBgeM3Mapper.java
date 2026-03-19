package org.xiaoyu.xchatmind.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xiaoyu.xchatmind.model.entity.ChunkBgeM3;

import java.util.List;

@Mapper
public interface ChunkBgeM3Mapper {
    List<ChunkBgeM3> similaritySearch(
            @Param("kbId") String kbId,
            @Param("vectorLiteral") String vectorLiteral,
            @Param("limit") int limit);

    int insert(ChunkBgeM3 chunk);
}
