package com.edusphere.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public String storeSyllabus(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir, "syllabi");
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String extension = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                    : "";
            String storedFilename = UUID.randomUUID() + extension;

            Path targetPath = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/files/syllabi/" + storedFilename;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store the uploaded file: " + e.getMessage(), e);
        }
    }
}
