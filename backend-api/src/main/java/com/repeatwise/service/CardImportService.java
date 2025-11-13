package com.repeatwise.service;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.repeatwise.entity.AsyncJob;
import com.repeatwise.enums.DuplicateHandlingPolicy;

/**
 * Service xử lý import thẻ (UC-021).
 */
public interface CardImportService {

    /**
     * Import đồng bộ (<= ngưỡng cho phép).
     *
     * @param deckId deck đích
     * @param userId người dùng
     * @param file   file upload
     * @param policy chính sách trùng thẻ
     * @return kết quả import
     */
    ImportResult importSync(UUID deckId, UUID userId, MultipartFile file, DuplicateHandlingPolicy policy);

    /**
     * Tự động quyết định sync/async theo số dòng.
     */
    ImportResponse importCards(UUID deckId, UUID userId, MultipartFile file, DuplicateHandlingPolicy policy);

    /**
     * Khởi tạo job import bất đồng bộ (> ngưỡng).
     *
     * @param deckId deck đích
     * @param userId người dùng
     * @param file   file upload
     * @param policy chính sách trùng thẻ
     * @return job đã tạo
     */
    AsyncJob startAsyncImport(UUID deckId, UUID userId, MultipartFile file, DuplicateHandlingPolicy policy);

    /**
     * Lấy resource báo cáo lỗi.
     */
    Resource loadErrorReport(UUID jobId, UUID userId);

    /**
     * Xóa file tạm (dùng cho test/cleanup).
     */
    void deleteFile(Path path);

    /**
     * Kết quả import tổng hợp (sync hoặc async).
     */
    record ImportResponse(
            ImportResult summary,
            AsyncJob job) {

        public boolean isAsync() {
            return this.job != null && (this.summary == null);
        }
    }

    /**
     * Kết quả import.
     *
     * @param imported        số thẻ tạo/cập nhật
     * @param skipped         số dòng bị bỏ qua
     * @param failed          số dòng lỗi
     * @param totalRows       tổng số dòng xử lý
     * @param duplicatePolicy chính sách trùng
     * @param errorReportPath đường dẫn báo cáo lỗi (có thể null)
     * @param jobId           mã job (có thể null khi sync)
     */
    record ImportResult(
            int imported,
            int skipped,
            int failed,
            int totalRows,
            DuplicateHandlingPolicy duplicatePolicy,
            Path errorReportPath,
            UUID jobId) {
    }
}

