package com.repeatwise.service;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.repeatwise.enums.ExportFormat;

/**
 * Service lưu trữ file cho import/export.
 */
public interface FileStorageService {

    Path saveImportUpload(UUID jobId, MultipartFile file);

    Path resolveImportErrorReport(UUID jobId);

    Path resolveExportFile(UUID jobId, ExportFormat format);

    Resource loadAsResource(Path path);

    void deleteQuietly(Path path);
}

