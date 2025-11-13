package com.repeatwise.controller;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repeatwise.entity.User;
import com.repeatwise.enums.ExportFormat;
import com.repeatwise.service.CardExportService;
import com.repeatwise.service.CardImportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ImportExportFileController {

    private final CardImportService cardImportService;
    private final CardExportService cardExportService;

    @GetMapping("/imports/{jobId}/error-report")
    @Operation(summary = "Tải báo cáo lỗi import")
    public ResponseEntity<Resource> downloadErrorReport(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal User user) {
        final Resource resource = this.cardImportService.loadErrorReport(jobId, user.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"import-error-" + jobId + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @GetMapping("/exports/{jobId}/download")
    @Operation(summary = "Tải file export")
    public ResponseEntity<Resource> downloadExport(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal User user) {
        final Resource resource = this.cardExportService.loadExportFile(jobId, user.getId());
        final String filename = "export-" + jobId;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
