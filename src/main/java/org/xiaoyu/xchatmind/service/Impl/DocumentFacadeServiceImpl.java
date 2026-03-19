package org.xiaoyu.xchatmind.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xiaoyu.xchatmind.converter.DocumentConverter;
import org.xiaoyu.xchatmind.exception.BizException;
import org.xiaoyu.xchatmind.mapper.ChunkBgeM3Mapper;
import org.xiaoyu.xchatmind.mapper.DocumentMapper;
import org.xiaoyu.xchatmind.model.dto.DocumentDTO;
import org.xiaoyu.xchatmind.model.entity.ChunkBgeM3;
import org.xiaoyu.xchatmind.model.entity.Document;
import org.xiaoyu.xchatmind.model.request.CreateDocumentRequest;
import org.xiaoyu.xchatmind.model.request.UpdateDocumentRequest;
import org.xiaoyu.xchatmind.model.response.CreateDocumentResponse;
import org.xiaoyu.xchatmind.model.response.GetDocumentResponse;
import org.xiaoyu.xchatmind.model.vo.DocumentVO;
import org.xiaoyu.xchatmind.service.DocumentFacadeService;
import org.xiaoyu.xchatmind.service.DocumentStorageService;
import org.xiaoyu.xchatmind.service.MarkDownParseService;
import org.xiaoyu.xchatmind.service.RagService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentFacadeServiceImpl implements DocumentFacadeService {
    private final DocumentMapper documentMapper;
    private final DocumentConverter documentConverter;
    private final DocumentStorageService documentStorageService;
    private final MarkDownParseService markdownParserService;
    private final ChunkBgeM3Mapper chunkBgeM3Mapper;
    private final RagService ragService;

    @Override
    public GetDocumentResponse getDocuments() {
        List<Document> documents = documentMapper.selectAll();
        List<DocumentVO> result = new ArrayList<>();

        for (Document document : documents) {
            try {
                DocumentVO vo = documentConverter.toVO(document);
                result.add(vo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return GetDocumentResponse.builder()
                .documents(result.toArray(new DocumentVO[0]))
                .build();
    }

    @Override
    public GetDocumentResponse getDocumentByKbId(String kbId) {
        List<Document> documents = documentMapper.selectByKbId(kbId);
        List<DocumentVO> result = new ArrayList<>();

        for (Document document : documents) {
            try {
                DocumentVO vo = documentConverter.toVO(document);
                result.add(vo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return GetDocumentResponse.builder()
                .documents(result.toArray(new DocumentVO[0]))
                .build();
    }

    @Override
    public CreateDocumentResponse createDocument(CreateDocumentRequest request) {
        try {
            // 先转换为DTO
            DocumentDTO documentDTO = documentConverter.toDTO(request);

            // 转换为实体
            Document document = documentConverter.toEntity(documentDTO);

            // 设置时间
            LocalDateTime now = LocalDateTime.now();
            document.setCreatedAt(now);
            document.setUpdatedAt(now);

            // 插入数据库
            int result = documentMapper.insert(document);
            if (result <= 0) {
                throw new BizException("创建文档失败");
            }

            // 生成id并返回
            return CreateDocumentResponse.builder()
                    .documentId(document.getId())
                    .build();
        } catch (JsonProcessingException e) {
            throw new BizException("创建文档时发生序列化错误: " + e.getMessage());
        }
    }

    @Override
    public CreateDocumentResponse uploadDocument(String kbId, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new BizException("上传的文件为空");
            }

            // 提取文件信息
            String originalFileName = file.getOriginalFilename();
            String fileType = getFileType(originalFileName);
            long fileSize = file.getSize();

            // 创建文档记录
            DocumentDTO documentDTO = DocumentDTO.builder()
                    .kbId(kbId)
                    .filename(originalFileName)
                    .filetype(fileType)
                    .size(fileSize)
                    .build();

            // 转换为对应的实体
            Document document = documentConverter.toEntity(documentDTO);

            // 设置时间信息
            LocalDateTime now = LocalDateTime.now();
            document.setCreatedAt(now);
            document.setUpdatedAt(now);

            // 插入数据库
            int result = documentMapper.insert(document);
            if (result <= 0) {
                throw new BizException("创建文档失败");
            }

            String documentId = document.getId();

            // 保存文件
            String filePath = documentStorageService.saveFile(kbId, documentId, file);

            DocumentDTO.MetaData metaData = new DocumentDTO.MetaData();
            metaData.setFilePath(filePath);
            documentDTO.setMetadata(metaData);
            documentDTO.setId(documentId);
            documentDTO.setCreatedAt(now);
            documentDTO.setUpdatedAt(now);

            Document updatedDocument = documentConverter.toEntity(documentDTO);
            updatedDocument.setId(documentId);
            updatedDocument.setCreatedAt(now);
            updatedDocument.setUpdatedAt(now);

            documentMapper.updateById(updatedDocument);

            log.info("文档上传成功: kbId={}, documentId={}, filename={}", kbId, documentId, originalFileName);

            if ("md".equalsIgnoreCase(fileType) || "markdown".equalsIgnoreCase(fileType)) {
                processMarkdownDocument(kbId, documentId, filePath);
            } else {
                // 处理其他类型的文件
                log.warn("待新增处理的文件类型: {}", fileType);
            }

            return CreateDocumentResponse.builder()
                    .documentId(documentId)
                    .build();

        } catch (JsonProcessingException e) {
            throw new BizException("上传文档时发生序列化错误: " + e.getMessage());
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BizException("文件保存失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteDocument(String documentId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BizException("文档不存在" + documentId);
        }

        try {
            DocumentDTO documentDTO = documentConverter.toDTO(document);
            if (documentDTO.getMetadata() != null && documentDTO.getMetadata().getFilePath() != null) {
                String filePath = documentDTO.getMetadata().getFilePath();
                documentStorageService.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.warn("删除文件失败，继续删除文档记录: documentId={}, error={}", documentId, e.getMessage());
            // 即使文件删除失败，也继续删除数据库记录
        }

        // 删除文档记录
        int result = documentMapper.deleteById(documentId);
        if (result <= 0) {
            throw new BizException("删除文档失败" + documentId);
        }
    }

    @Override
    public void updateDocument(String documentId, UpdateDocumentRequest request) {
        try {
            // 查询文档
            Document existingDocument = documentMapper.selectById(documentId);
            if (existingDocument == null) {
                throw new BizException("文档不存在" + documentId);
            }

            // 转换为DTO
            DocumentDTO documentDTO = documentConverter.toDTO(existingDocument);

            // 更新DTO
            documentConverter.updateDTOFromRequest(documentDTO, request);

            // 转换为实体
            Document updateDocument = documentConverter.toEntity(documentDTO);

            // 设置id、kbId、时间啊等
            updateDocument.setId(existingDocument.getId());
            updateDocument.setKbId(existingDocument.getKbId());
            updateDocument.setCreatedAt(existingDocument.getCreatedAt());
            updateDocument.setUpdatedAt(LocalDateTime.now());

            // 更新数据库
            int result = documentMapper.updateById(updateDocument);
            if (result <= 0) {
                throw new BizException("更新文档失败" + documentId);
            }
        } catch (JsonProcessingException e) {
            throw new BizException("更新文档时发生序列化错误: " + e.getMessage());
        }
    }

    /** helper method. */
    // get the file type
    private String getFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "unknown";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 解析 markdown 文档
     * @param kbId
     * @param documentId
     * @param filePath
     */
    private void processMarkdownDocument(String kbId, String documentId, String filePath) {
        try {
            log.info("开始解析 markdown 文档: kbId={}, documentId={}", kbId, documentId);

            // 读取文件内容
            Path path = documentStorageService.getFilePath(filePath);
            try (InputStream inputStream = Files.newInputStream(path)) {
                // 解析 Markdown 文件
                List<MarkDownParseService.MarkdownSection> sections = markdownParserService.parseMarkdown(inputStream);

                if (sections.isEmpty()) {
                    log.warn("Markdown 文件为空: kbId={}, documentId={}", kbId, documentId);
                    return;
                }

                LocalDateTime now = LocalDateTime.now();
                int chunkCount = 0;

                // 为每个章节生成 chunk
                for (MarkDownParseService.MarkdownSection section : sections) {
                    String title = section.getTitle();
                    String content = section.getContent();

                    if (title == null || title.trim().isEmpty()) {
                        continue;
                    }

                    // 对标题进行 embedding
                    float[] embedding = ragService.embed(title);

                    // 创建 ChunkBgeM3 实体
                    ChunkBgeM3 chunk = ChunkBgeM3.builder()
                            .kbId(kbId)
                            .docId(documentId)
                            .content(content != null ? content : "")
                            .metadata(null) // 可以存储标题信息到 metadata
                            .embedding(embedding)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();

                    // 插入数据库
                    int result = chunkBgeM3Mapper.insert(chunk);

                    if (result > 0) {
                        chunkCount++;
                        log.debug("创建 chunk 成功: title={}, chunkId={}", title, chunk.getId());
                    } else {
                        log.warn("创建 chunk 失败: title={}", title);
                    }
                }
                log.info("Markdown 文档处理完成: documentId={}, 共生成 {} 个 chunks", documentId, chunkCount);
            }
        } catch (Exception e) {
            log.error("处理 Markdown 文档时发生错误: kbId={}, documentId={}", kbId, documentId, e);
        }
    }
}
