package com.repeatwise.service;

import java.util.UUID;

import com.repeatwise.entity.AsyncJob;
import com.repeatwise.enums.AsyncJobStatus;
import com.repeatwise.enums.AsyncJobType;

/**
 * Service quản lý async job (import/export).
 */
public interface AsyncJobService {

    AsyncJob getJob(UUID jobId, UUID userId);

    AsyncJob save(AsyncJob job);

    AsyncJob updateStatus(UUID jobId, AsyncJobStatus status);

    AsyncJobResponse toResponse(AsyncJob job);

    /**
     * DTO trả về cho client.
     */
    record AsyncJobResponse(
            UUID jobId,
            AsyncJobType jobType,
            AsyncJobStatus status,
            Integer totalRows,
            Integer processedRows,
            Integer successCount,
            Integer skippedCount,
            Integer failedCount,
            Integer progress,
            String message,
            String resultPath,
            String errorReportPath) {
    }
}

