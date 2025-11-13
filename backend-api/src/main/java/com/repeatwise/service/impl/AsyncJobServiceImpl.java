package com.repeatwise.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.entity.AsyncJob;
import com.repeatwise.enums.AsyncJobStatus;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.repository.AsyncJobRepository;
import com.repeatwise.service.AsyncJobService;

import lombok.RequiredArgsConstructor;

/**
 * Triển khai AsyncJobService.
 */
@Service
@RequiredArgsConstructor
public class AsyncJobServiceImpl implements AsyncJobService {

    private final AsyncJobRepository asyncJobRepository;

    @Override
    @Transactional(readOnly = true)
    public AsyncJob getJob(UUID jobId, UUID userId) {
        return this.asyncJobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
    }

    @Override
    @Transactional
    public AsyncJob save(AsyncJob job) {
        job.initializeCounts();
        return this.asyncJobRepository.save(job);
    }

    @Override
    @Transactional
    public AsyncJob updateStatus(UUID jobId, AsyncJobStatus status) {
        final var job = this.asyncJobRepository.findById(jobId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
        job.setStatus(status);
        return this.asyncJobRepository.save(job);
    }

    @Override
    public AsyncJobResponse toResponse(AsyncJob job) {
        final var total = safe(job.getTotalRows());
        final var processed = safe(job.getProcessedRows());
        final var success = safe(job.getSuccessCount());
        final var skipped = safe(job.getSkippedCount());
        final var failed = safe(job.getFailedCount());
        final var progress = total > 0 ? Math.min(100, (processed * 100) / total) : null;
        return new AsyncJobResponse(
                job.getId(),
                job.getJobType(),
                job.getStatus(),
                nullable(total),
                nullable(processed),
                nullable(success),
                nullable(skipped),
                nullable(failed),
                progress,
                job.getMessage(),
                job.getResultPath(),
                job.getErrorReportPath());
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }

    private Integer nullable(int value) {
        return value == 0 ? null : value;
    }
}

