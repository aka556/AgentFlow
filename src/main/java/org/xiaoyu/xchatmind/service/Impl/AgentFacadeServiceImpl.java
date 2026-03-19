package org.xiaoyu.xchatmind.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.JacksonYAMLParseException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xiaoyu.xchatmind.converter.AgentConverter;
import org.xiaoyu.xchatmind.exception.BizException;
import org.xiaoyu.xchatmind.mapper.AgentMapper;
import org.xiaoyu.xchatmind.model.dto.AgentDTO;
import org.xiaoyu.xchatmind.model.entity.Agent;
import org.xiaoyu.xchatmind.model.request.CreateAgentRequest;
import org.xiaoyu.xchatmind.model.request.UpdateAgentRequest;
import org.xiaoyu.xchatmind.model.response.CreateAgentResponse;
import org.xiaoyu.xchatmind.model.response.GetAgentResponse;
import org.xiaoyu.xchatmind.model.vo.AgentVO;
import org.xiaoyu.xchatmind.service.AgentFacadeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AgentFacadeServiceImpl implements AgentFacadeService {
    private final AgentMapper agentMapper;
    private final AgentConverter agentConverter;

    @Override
    public GetAgentResponse getAgents() {
        List<Agent> agents = agentMapper.selectAll();
        List<AgentVO> result = new ArrayList<>();

        for (Agent agent : agents) {
            try {
                AgentVO vo = agentConverter.toVO(agent);
                result.add(vo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return GetAgentResponse.builder().agents(result.toArray(new AgentVO[0])).build();
    }

    @Override
    public CreateAgentResponse createAgent(CreateAgentRequest request) {
        try {
            // 转换为AgentDTO
            AgentDTO dto = agentConverter.toDTO(request);

            // 转换为Agent实体
            Agent agent = agentConverter.toEntity(dto);

            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            agent.setCreatedAt(now);
            agent.setUpdatedAt(now);

            // 保存
            int result = agentMapper.insert(agent);
            if (result <= 0) {
                throw new BizException("创建Agent失败");
            }

            return CreateAgentResponse.builder().agentId(agent.getId()).build();
        } catch (JsonProcessingException e) {
            throw new BizException("创建 agent 时发生序列化错误: " + e.getMessage());
        }
    }

    @Override
    public void deleteAgent(String agentId) {
        // 检查Agent是否存在
        Agent agent = agentMapper.selectById(agentId);
        if (agent == null) {
            throw new BizException("Agent不存在" + agentId);
        }

        int result = agentMapper.deleteById(agentId);
        if (result <= 0) {
            throw new BizException("删除Agent失败");
        }
    }

    @Override
    public void updateAgent(String agentId, UpdateAgentRequest request) {
        try {
            // 查询现有Agent
            Agent existingAgent = agentMapper.selectById(agentId);
            if (existingAgent == null) {
                throw new BizException("Agent不存在" + agentId);
            }

            // 转换为AgentDTO
            AgentDTO dto = agentConverter.toDTO(existingAgent);

            // 更新
            agentConverter.updateDTOFromRequest(dto, request);

            // 转换为agent实体
            Agent updateAgent = agentConverter.toEntity(dto);

            // 保留原有ID和时间信息
            updateAgent.setId(existingAgent.getId());
            updateAgent.setCreatedAt(existingAgent.getCreatedAt());
            updateAgent.setUpdatedAt(LocalDateTime.now());

            // 更新数据库
            int result = agentMapper.updateById(updateAgent);
            if (result <= 0) {
                throw new BizException("更新Agent失败");
            }
        } catch (JsonProcessingException e) {
            throw new BizException("更新 agent 时发生序列化错误: " + e.getMessage());
        }
    }
}
