package com.repeatwise.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.repeatwise.entity.base.BaseEntity;
import com.repeatwise.enums.AsyncJobStatus;
import com.repeatwise.enums.AsyncJobType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Bảng lưu trạng thái job bất đồng bộ cho import/export.
 */
@Entity
@Table(name = "async_jobs")
@Getter
@Setter
public class AsyncJob extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 32)
    private AsyncJobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private AsyncJobStatus status;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "deck_id", nullable = false)
    private UUID deckId;

    @Column(name = "total_rows")
    private Integer totalRows;

    @Column(name = "processed_rows")
    private Integer processedRows;

    @Column(name = "success_count")
    private Integer successCount;

    @Column(name = "skipped_count")
    private Integer skippedCount;

    @Column(name = "failed_count")
    private Integer failedCount;

    @Column(name = "duplicate_policy", length = 32)
    private String duplicatePolicy;

    @Column(name = "export_format", length = 16)
    private String exportFormat;

    @Column(name = "export_scope", length = 16)
    private String exportScope;

    @Column(name = "payload_path", length = 500)
    private String payloadPath;

    @Column(name = "result_path", length = 500)
    private String resultPath;

    @Column(name = "error_report_path", length = 500)
    private String errorReportPath;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public void initializeCounts() {
        this.totalRows = defaultToZero(this.totalRows);
        this.processedRows = defaultToZero(this.processedRows);
        this.successCount = defaultToZero(this.successCount);
        this.skippedCount = defaultToZero(this.skippedCount);
        this.failedCount = defaultToZero(this.failedCount);
    }

    private int defaultToZero(Integer value) {
        return value == null ? 0 : value;
    }
}

