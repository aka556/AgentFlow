package org.xiaoyu.xchatmind.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xiaoyu.xchatmind.model.entity.ChatMessage;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    int insert(ChatMessage chatMessage);

    List<ChatMessage> selectBySessionIdRecently(String chatSessionId, int limit);

    List<ChatMessage> selectBySessionId(String sessionId);

    ChatMessage selectById(String id);

    int updateById(ChatMessage chatMessage);

    int deleteById(String id);
}
