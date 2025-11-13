package com.repeatwise.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repeatwise.dto.response.job.AsyncJobResponseDto;
import com.repeatwise.entity.AsyncJob;
import com.repeatwise.entity.User;
import com.repeatwise.service.AsyncJobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Async Jobs", description = "Trạng thái job import/export")
public class JobController {

    private final AsyncJobService asyncJobService;

    @GetMapping("/{jobId}")
    @Operation(summary = "Lấy trạng thái job")
    public AsyncJobResponseDto getJob(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal User user) {
        final AsyncJob job = this.asyncJobService.getJob(jobId, user.getId());
        final var response = this.asyncJobService.toResponse(job);
        final String jobIdStr = response.jobId().toString();
        final String downloadUrl = response.resultPath() != null
                ? "/v1/exports/%s/download".formatted(jobIdStr)
                : null;
        final String errorReportUrl = response.errorReportPath() != null
                ? "/v1/imports/%s/error-report".formatted(jobIdStr)
                : null;
        return AsyncJobResponseDto.builder()
                .jobId(jobIdStr)
                .jobType(response.jobType().name())
                .status(response.status().name())
                .totalRows(response.totalRows())
                .processedRows(response.processedRows())
                .successCount(response.successCount())
                .skippedCount(response.skippedCount())
                .failedCount(response.failedCount())
                .progress(response.progress())
                .message(response.message())
                .downloadUrl(downloadUrl)
                .errorReportUrl(errorReportUrl)
                .build();
    }
}
