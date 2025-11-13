package com.repeatwise.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.repeatwise.config.properties.AppProperties;
import com.repeatwise.enums.ExportFormat;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.service.FileStorageService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Triển khai lưu trữ file cục bộ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final AppProperties appProperties;

    private Path basePath;
    private Path importUploadBase;
    private Path importErrorBase;
    private Path exportBase;

    @PostConstruct
    void init() {
        final var storage = this.appProperties.getStorage();
        this.basePath = Path.of(storage.getBasePath()).toAbsolutePath().normalize();
        this.importUploadBase = this.basePath.resolve(storage.getImportUploadsDir()).normalize();
        this.importErrorBase = this.basePath.resolve(storage.getImportErrorDir()).normalize();
        this.exportBase = this.basePath.resolve(storage.getExportDir()).normalize();

        createDirectories(this.basePath);
        createDirectories(this.importUploadBase);
        createDirectories(this.importErrorBase);
        createDirectories(this.exportBase);
    }

    @Override
    public Path saveImportUpload(UUID jobId, MultipartFile file) {
        final var targetDir = this.importUploadBase.resolve(jobId.toString());
        createDirectories(targetDir);
        final var sanitizedName = sanitizeFilename(file.getOriginalFilename());
        final var targetFile = targetDir.resolve(sanitizedName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            log.error("Không thể lưu file upload import cho job {}", jobId, ex);
            throw new RepeatWiseException(RepeatWiseError.IMPORT_INVALID_FORMAT, ex);
        }
        return targetFile;
    }

    @Override
    public Path resolveImportErrorReport(UUID jobId) {
        createDirectories(this.importErrorBase);
        return this.importErrorBase.resolve(jobId + ".csv");
    }

    @Override
    public Path resolveExportFile(UUID jobId, ExportFormat format) {
        createDirectories(this.exportBase);
        return this.exportBase.resolve(jobId + "." + format.getExtension());
    }

    @Override
    public Resource loadAsResource(Path path) {
        try {
            final var normalized = path.toAbsolutePath().normalize();
            if (!Files.exists(normalized)) {
                throw new RepeatWiseException(RepeatWiseError.RESOURCE_NOT_FOUND, normalized);
            }
            return new UrlResource(normalized.toUri());
        } catch (IOException ex) {
            log.error("Không thể đọc resource {}", path, ex);
            throw new RepeatWiseException(RepeatWiseError.INTERNAL_SERVER_ERROR, ex);
        }
    }

    @Override
    public void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            log.warn("Không thể xóa file {}: {}", path, ex.getMessage());
        }
    }

    private void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException ex) {
            log.error("Không thể tạo thư mục lưu trữ {}", path, ex);
            throw new RepeatWiseException(RepeatWiseError.INTERNAL_SERVER_ERROR, ex);
        }
    }

    private String sanitizeFilename(String filename) {
        final var baseName = FilenameUtils.getBaseName(filename != null ? filename : "upload");
        final var extension = FilenameUtils.getExtension(filename != null ? filename : "");
        final var safeBase = baseName.isBlank() ? "upload" : baseName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
        if (extension.isBlank()) {
            return safeBase;
        }
        return safeBase + "." + extension;
    }
}

