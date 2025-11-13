package com.repeatwise.controller;

import java.util.Locale;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.repeatwise.dto.response.importer.ImportCardsResponse;
import com.repeatwise.dto.response.job.AsyncJobResponseDto;
import com.repeatwise.entity.AsyncJob;
import com.repeatwise.entity.User;
import com.repeatwise.enums.DuplicateHandlingPolicy;
import com.repeatwise.enums.ExportFormat;
import com.repeatwise.enums.ExportScope;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.service.AsyncJobService;
import com.repeatwise.service.CardExportService;
import com.repeatwise.service.CardExportService.ExportResponse;
import com.repeatwise.service.CardExportService.ExportResult;
import com.repeatwise.service.CardImportService;
import com.repeatwise.service.CardImportService.ImportResponse;
import com.repeatwise.service.CardImportService.ImportResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controller cho import/export thẻ theo deck (UC-021 & UC-022).
 */
@RestController
@RequestMapping("/v1/decks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Deck Import/Export", description = "Import/Export thẻ theo deck")
public class DeckImportExportController {

    private final CardImportService cardImportService;
    private final CardExportService cardExportService;
    private final AsyncJobService asyncJobService;
    private final MessageSource messageSource;

    @PostMapping(path = "/{deckId}/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import thẻ", description = "Import thẻ từ file CSV/XLSX vào deck")
    @ApiResponse(responseCode = "200", description = "Import đồng bộ thành công")
    @ApiResponse(responseCode = "202", description = "Import đang xử lý async")
    public ResponseEntity<?> importCards(
            @PathVariable UUID deckId,
            @AuthenticationPrincipal User user,
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "duplicatePolicy", defaultValue = "SKIP") String duplicatePolicy) {
        final var policy = parseDuplicatePolicy(duplicatePolicy);
        final var response = this.cardImportService.importCards(deckId, user.getId(), file, policy);
        if (response.isAsync()) {
            final var jobDto = buildJobResponse(response.job());
            return ResponseEntity.accepted().body(jobDto);
        }
        final var summary = response.summary();
        final var body = buildImportResponse(summary, policy.name());
        return ResponseEntity.ok(body);
    }

    @GetMapping(path = "/{deckId}/export")
    @Operation(summary = "Export thẻ", description = "Export thẻ ra CSV hoặc XLSX")
    @ApiResponse(responseCode = "200", description = "Export đồng bộ thành công")
    @ApiResponse(responseCode = "202", description = "Export đang xử lý async", content = @Content(schema = @Schema(implementation = AsyncJobResponseDto.class)))
    public ResponseEntity<?> exportCards(
            @PathVariable UUID deckId,
            @AuthenticationPrincipal User user,
            @RequestParam(name = "format", defaultValue = "CSV") String format,
            @RequestParam(name = "scope", defaultValue = "ALL") String scope) {
        final var exportFormat = parseExportFormat(format);
        final var exportScope = parseExportScope(scope);
        final ExportResponse exportResponse = this.cardExportService.exportCards(deckId, user.getId(), exportFormat, exportScope);
        if (exportResponse.isAsync()) {
            final var jobDto = buildJobResponse(exportResponse.job());
            return ResponseEntity.accepted().body(jobDto);
        }
        final ExportResult result = exportResponse.result();
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.contentType()));
        headers.setContentLength(result.size());
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.filename() + "\"");
        return new ResponseEntity<>(result.resource(), headers, HttpStatus.OK);
    }

    private ImportCardsResponse buildImportResponse(ImportResult summary, String policy) {
        final Locale locale = LocaleContextHolder.getLocale();
        final String message = this.messageSource.getMessage(
                "success.card.imported",
                new Object[] { summary.imported() },
                locale);
        final String jobId = summary.jobId() != null ? summary.jobId().toString() : null;
        final String errorReportUrl = (summary.errorReportPath() != null && jobId != null)
                ? "/v1/imports/%s/error-report".formatted(jobId)
                : null;
        return ImportCardsResponse.builder()
                .imported(summary.imported())
                .skipped(summary.skipped())
                .failed(summary.failed())
                .totalRows(summary.totalRows())
                .duplicatePolicy(policy)
                .message(message)
                .errorReportUrl(errorReportUrl)
                .jobId(jobId)
                .build();
    }

    private AsyncJobResponseDto buildJobResponse(AsyncJob job) {
        final var jobResponse = this.asyncJobService.toResponse(job);
        final String jobId = jobResponse.jobId().toString();
        final String downloadUrl = jobResponse.resultPath() != null
                ? "/v1/exports/%s/download".formatted(jobId)
                : null;
        final String errorReportUrl = jobResponse.errorReportPath() != null
                ? "/v1/imports/%s/error-report".formatted(jobId)
                : null;
        return AsyncJobResponseDto.builder()
                .jobId(jobId)
                .jobType(jobResponse.jobType().name())
                .status(jobResponse.status().name())
                .totalRows(jobResponse.totalRows())
                .processedRows(jobResponse.processedRows())
                .successCount(jobResponse.successCount())
                .skippedCount(jobResponse.skippedCount())
                .failedCount(jobResponse.failedCount())
                .progress(jobResponse.progress())
                .message(jobResponse.message())
                .downloadUrl(downloadUrl)
                .errorReportUrl(errorReportUrl)
                .build();
    }

    private DuplicateHandlingPolicy parseDuplicatePolicy(String value) {
        try {
            return DuplicateHandlingPolicy.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_INVALID_FORMAT, value);
        }
    }

    private ExportFormat parseExportFormat(String value) {
        try {
            return ExportFormat.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new RepeatWiseException(RepeatWiseError.EXPORT_INVALID_FORMAT, value);
        }
    }

    private ExportScope parseExportScope(String value) {
        try {
            return ExportScope.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new RepeatWiseException(RepeatWiseError.EXPORT_INVALID_SCOPE, value);
        }
    }
}
