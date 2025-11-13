package com.repeatwise.service;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.core.io.Resource;

import com.repeatwise.entity.AsyncJob;
import com.repeatwise.enums.ExportFormat;
import com.repeatwise.enums.ExportScope;

/**
 * Service xử lý export thẻ (UC-022).
 */
public interface CardExportService {

    /**
     * Export đồng bộ (<= ngưỡng) trả về file Resource.
     */
    ExportResult exportSync(UUID deckId, UUID userId, ExportFormat format, ExportScope scope);

    /**
     * Quyết định sync/async dựa trên kích thước dữ liệu.
     */
    ExportResponse exportCards(UUID deckId, UUID userId, ExportFormat format, ExportScope scope);

    /**
     * Khởi tạo job export bất đồng bộ (> ngưỡng).
     */
    AsyncJob startAsyncExport(UUID deckId, UUID userId, ExportFormat format, ExportScope scope);

    /**
     * Tải file export đã sinh.
     */
    Resource loadExportFile(UUID jobId, UUID userId);

    /**
     * Thông tin export đồng bộ.
     */
    record ExportResult(
            Resource resource,
            String filename,
            String contentType,
            long size,
            UUID jobId,
            Path filePath,
            long totalRows) {
    }

    record ExportResponse(
            ExportResult result,
            AsyncJob job) {

        public boolean isAsync() {
            return this.job != null && this.result == null;
        }
    }
}

