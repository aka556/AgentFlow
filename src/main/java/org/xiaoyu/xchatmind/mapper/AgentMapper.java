package org.xiaoyu.xchatmind.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xiaoyu.xchatmind.model.entity.Agent;

import java.util.List;

@Mapper
public interface AgentMapper {
    Agent selectById(String agentId);

    List<Agent> selectAll();

    int insert(Agent agent);

    int deleteById(String agentId);

    int updateById(Agent updateAgent);
}
