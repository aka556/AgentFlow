package org.xiaoyu.xchatmind.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xiaoyu.xchatmind.converter.KnowledgeBaseConverter;
import org.xiaoyu.xchatmind.exception.BizException;
import org.xiaoyu.xchatmind.mapper.KnowledgeBaseMapper;
import org.xiaoyu.xchatmind.model.dto.KnowledgeBaseDTO;
import org.xiaoyu.xchatmind.model.entity.KnowledgeBase;
import org.xiaoyu.xchatmind.model.request.CreateKnowledgeBaseRequest;
import org.xiaoyu.xchatmind.model.request.UpdateKnowledgeBaseRequest;
import org.xiaoyu.xchatmind.model.response.CreateAgentResponse;
import org.xiaoyu.xchatmind.model.response.CreateKnowledgeBaseResponse;
import org.xiaoyu.xchatmind.model.response.GetKnowledgeBaseResponse;
import org.xiaoyu.xchatmind.model.vo.KnowledgeBaseVO;
import org.xiaoyu.xchatmind.service.KnowledgeBaseFacadeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class KnowledgeBaseFacadeServiceImpl implements KnowledgeBaseFacadeService {
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final KnowledgeBaseConverter knowledgeBaseConverter;


    @Override
    public GetKnowledgeBaseResponse getKnowledgeBases() {
        List<KnowledgeBase> knowledgeBases = knowledgeBaseMapper.selectAll();
        List<KnowledgeBaseVO> result = new ArrayList<>();

        for (KnowledgeBase knowledgeBase : knowledgeBases) {
            try {
                KnowledgeBaseVO vo = knowledgeBaseConverter.toVO(knowledgeBase);
                result.add(vo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return GetKnowledgeBaseResponse.builder()
                .knowledgeBases(result.toArray(new KnowledgeBaseVO[0]))
                .build();
    }

    @Override
    public CreateKnowledgeBaseResponse createKnowledgeBase(CreateKnowledgeBaseRequest request) {
        try {
            // 将请求转换为DTO
            KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseConverter.toDTO(request);

            // 将DTO转换为实体
            KnowledgeBase knowledgeBase = knowledgeBaseConverter.toEntity(knowledgeBaseDTO);

            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            knowledgeBase.setCreatedAt(now);
            knowledgeBase.setUpdatedAt(now);

            // 插入数据库
            int result = knowledgeBaseMapper.insert(knowledgeBase);
            if (result <= 0) {
                throw new RuntimeException("创建知识库失败");
            }

            // 创建成功，返回创建的Agent ID
            return CreateKnowledgeBaseResponse.builder()
                    .knowledgeBaseId(knowledgeBase.getId())
                    .build();
        } catch (JsonProcessingException e) {
            throw new BizException("创建知识库时发生序列化错误: " + e.getMessage());
        }
    }

    @Override
    public void deleteKnowledgeBase(String knowledgeBaseId) {
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseId);
        if (knowledgeBase == null) {
            throw new BizException("知识库不存在" + knowledgeBaseId);
        }

        // 尝试删除
        int result = knowledgeBaseMapper.deleteById(knowledgeBaseId);
        if (result <= 0) {
            throw new RuntimeException("删除知识库失败");
        }
    }

    @Override
    public void updateKnowledgeBase(String knowledgeBaseId, UpdateKnowledgeBaseRequest request) {
        try {
            KnowledgeBase existingKnowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseId);
            if (existingKnowledgeBase == null) {
                throw new BizException("知识库不存在" + knowledgeBaseId);
            }

            // 转换为DTO
            KnowledgeBaseDTO knowledgeBaseDTO = knowledgeBaseConverter.toDTO(existingKnowledgeBase);

            // 更新DTO
            knowledgeBaseConverter.updateDTOFromRequest(knowledgeBaseDTO, request);

            // 转换为实体
            KnowledgeBase updatedKnowledgeBase = knowledgeBaseConverter.toEntity(knowledgeBaseDTO);

            // 保留原有的id和时间
            updatedKnowledgeBase.setId(existingKnowledgeBase.getId());
            updatedKnowledgeBase.setCreatedAt(existingKnowledgeBase.getCreatedAt());
            updatedKnowledgeBase.setUpdatedAt(LocalDateTime.now());

            // 更新数据库
            int result = knowledgeBaseMapper.updateById(updatedKnowledgeBase);
            if (result <= 0) {
                throw new RuntimeException("更新知识库失败");
            }
        } catch (JsonProcessingException e) {
            throw new BizException("更新知识库时发生序列化错误: " + e.getMessage());
        }
    }
}
