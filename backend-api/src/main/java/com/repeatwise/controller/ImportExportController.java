package com.repeatwise.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.repeatwise.dto.request.card.ImportCardsRequest;
import com.repeatwise.dto.response.card.ImportResultResponse;
import com.repeatwise.log.LogEvent;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.IImportExportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Import/Export Management
 *
 * Requirements:
 * - UC-021: Import Cards
 * - UC-022: Export Cards
 *
 * Endpoints:
 * - POST /api/decks/{deckId}/import - Import cards from CSV/XLSX
 * - GET /api/decks/{deckId}/export - Export cards to CSV/XLSX
 *
 * @author RepeatWise Team
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ImportExportController {

    private final IImportExportService importExportService;

    // ==================== UC-021: Import Cards ====================

    /**
     * Import cards from CSV/XLSX file
     * UC-021: Import Cards
     *
     * Requirements:
     * - UC-021: Import Cards
     * - BR-IMP-01: File size <= 50MB
     * - BR-IMP-02: Row limit <= 10,000
     * - BR-IMP-03: Required columns: Front, Back
     *
     * Request:
     * - Multipart form with file and duplicatePolicy
     *
     * Response: 200 OK with import result
     *
     * @param deckId          Deck UUID
     * @param file            CSV or XLSX file
     * @param duplicatePolicy Duplicate handling policy (SKIP, REPLACE, KEEP_BOTH)
     * @return Import result response
     */
    @PostMapping(value = "/decks/{deckId}/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResultResponse> importCards(
            @PathVariable final UUID deckId,
            @RequestPart("file") final MultipartFile file,
            @RequestParam(value = "duplicatePolicy", defaultValue = "SKIP") final String duplicatePolicy) {

        final var userId = SecurityUtils.getCurrentUserId();

        log.info("event={} POST /api/decks/{}/import - Importing cards: fileName={}, duplicatePolicy={}, userId={}",
                LogEvent.START, deckId, file.getOriginalFilename(), duplicatePolicy, userId);

        final var request = ImportCardsRequest.builder()
                .duplicatePolicy(ImportCardsRequest.DuplicatePolicy.valueOf(duplicatePolicy.toUpperCase()))
                .build();

        final var response = this.importExportService.importCards(deckId, file, request, userId);

        log.info("event={} Import completed: imported={}, skipped={}, failed={}, userId={}",
                LogEvent.SUCCESS, response.getImported(), response.getSkipped(), response.getFailed(), userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-022: Export Cards ====================

    /**
     * Export cards to CSV/XLSX file
     * UC-022: Export Cards
     *
     * Requirements:
     * - UC-022: Export Cards
     * - Export scope: ALL or DUE_ONLY
     * - Format: CSV or XLSX
     *
     * Request Parameters:
     * - format: csv or xlsx (default: csv)
     * - scope: ALL or DUE_ONLY (default: ALL)
     *
     * Response: 200 OK with file download
     *
     * @param deckId Deck UUID
     * @param format Export format (csv or xlsx)
     * @param scope  Export scope (ALL or DUE_ONLY)
     * @return File resource for download
     */
    @GetMapping("/decks/{deckId}/export")
    public ResponseEntity<Resource> exportCards(
            @PathVariable final UUID deckId,
            @RequestParam(value = "format", defaultValue = "csv") final String format,
            @RequestParam(value = "scope", defaultValue = "ALL") final String scope) {

        final var userId = SecurityUtils.getCurrentUserId();

        log.info("event={} GET /api/decks/{}/export - Exporting cards: format={}, scope={}, userId={}",
                LogEvent.START, deckId, format, scope, userId);

        final var resource = this.importExportService.exportCards(deckId, format, scope, userId);

        final var filename = String.format("deck_%s_%s.%s",
                deckId.toString().substring(0, 8),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                format.toLowerCase());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
