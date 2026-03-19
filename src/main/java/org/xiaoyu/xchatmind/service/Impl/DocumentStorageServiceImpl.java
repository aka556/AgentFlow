package org.xiaoyu.xchatmind.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xiaoyu.xchatmind.service.DocumentStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class DocumentStorageServiceImpl implements DocumentStorageService {
    @Value("${document.storage.base-path:./data/documents}")
    private String baseStoragePath;

    @Override
    public String saveFile(String kbId, String documentId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件为空");
        }

        // 构建文件存储路径
        Path kbDir = Paths.get(baseStoragePath, kbId);
        Path documentDir = kbDir.resolve(documentId);

        // 检查目录是否存在
        Files.createDirectories(documentDir);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path targetPath = documentDir.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 返回相对路径
        String relativePath = Paths.get(kbId, documentId, uniqueFileName).toString().replace("\\", "/");
        log.info("文件保存成功: kbId={}, documentId={}, filename={}, path={}",
                kbId, documentId, originalFilename, relativePath);

        return relativePath;
    }

    @Override
    public Path getFilePath(String filePath) {
        // 解析文件路径
        return Paths.get(baseStoragePath, filePath);
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        Path fullPath = getFilePath(filePath);
        if (Files.exists(fullPath)) {
            Files.delete(fullPath);
            log.info("文件删除成功: {}", fullPath);

            // 删除空目录
            Path parentDir = fullPath.getParent();
            if (parentDir != null && Files.exists(parentDir)) {
                try {
                    Files.delete(parentDir);
                    log.info("空目录删除成功: {}", parentDir);
                } catch (IOException e) {
                    log.warn("删除空目录失败: {}", parentDir, e);
                }
            }
        } else {
            log.warn("文件不存在: {}", fullPath);
        }
    }
}
