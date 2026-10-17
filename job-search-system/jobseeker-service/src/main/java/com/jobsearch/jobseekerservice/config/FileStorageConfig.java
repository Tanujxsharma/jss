package com.jobsearch.jobseekerservice.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
@Getter
public class FileStorageConfig {

    @Value("${file.upload-dir:./uploads/resumes}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created upload directory: {}", path);
            }
        } catch (IOException e) {
            log.error("Could not initialize upload directory: {}", uploadDir, e);
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }
}
