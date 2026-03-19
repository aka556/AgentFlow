package org.xiaoyu.xchatmind.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xiaoyu.xchatmind.model.entity.ChatSession;

import java.util.List;

@Mapper
public interface ChatSessionMapper {
    List<ChatSession> selectAll();

    ChatSession selectById(String id);

    List<ChatSession> selectByAgentId(String agentId);

    int insert(ChatSession chatSession);

    int deleteById(String id);

    int updateById(ChatSession chatSession);
}
