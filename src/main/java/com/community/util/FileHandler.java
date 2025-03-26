package com.community.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileHandler {

    public String saveFile(MultipartFile file, String directoryName) {
        if (file == null || file.isEmpty()) return null;

        try {
            String uploadDir = System.getProperty("user.dir") + "/" + directoryName + "/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + fileName;

            file.transferTo(new File(filePath));

            // 프론트에서 접근할 URL 경로 반환
            return "/" + directoryName + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String urlPath) {
        if (urlPath == null) return;

        String realPath = System.getProperty("user.dir") + urlPath;
        File file = new File(realPath);
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException("파일 삭제 실패: " + realPath);
            }
        }
    }
}

