package org.xiaoyu.xchatmind.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentStorageService {
    String saveFile(String kbId, String documentId, MultipartFile file) throws IOException;

    Path getFilePath(String filePath);

    void deleteFile(String filePath) throws IOException;
}
