package com.repeatwise.dto.response.job;

import lombok.Builder;
import lombok.Getter;

/**
 * Thông tin job bất đồng bộ trả về cho client.
 */
@Getter
@Builder
public class AsyncJobResponseDto {
    private final String jobId;
    private final String jobType;
    private final String status;
    private final Integer totalRows;
    private final Integer processedRows;
    private final Integer successCount;
    private final Integer skippedCount;
    private final Integer failedCount;
    private final Integer progress;
    private final String message;
    private final String downloadUrl;
    private final String errorReportUrl;
}

