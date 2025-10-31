package com.repeatwise.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.repeatwise.dto.request.card.CreateCardRequest;
import com.repeatwise.dto.request.card.ImportCardsRequest;
import com.repeatwise.dto.response.card.ImportResultResponse;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.entity.Deck;
import com.repeatwise.entity.User;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.exception.ValidationException;
import com.repeatwise.log.LogEvent;
import com.repeatwise.repository.CardBoxPositionRepository;
import com.repeatwise.repository.CardRepository;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.BaseService;
import com.repeatwise.service.ICardService;
import com.repeatwise.service.IImportExportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of Import/Export Service
 *
 * Requirements:
 * - UC-021: Import Cards
 * - UC-022: Export Cards
 *
 * Business Rules:
 * - BR-IMP-01: File size <= 50MB
 * - BR-IMP-02: Row limit <= 10,000
 * - BR-IMP-03: Required columns: Front, Back
 * - BR-CARD-01: Front/Back required, <= 5000 chars
 *
 * @author RepeatWise Team
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ImportExportServiceImpl extends BaseService implements IImportExportService {

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final int MAX_ROWS = 10000;

    private final CardRepository cardRepository;
    private final CardBoxPositionRepository cardBoxPositionRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final ICardService cardService;

    // ==================== UC-021: Import Cards ====================

    @Override
    @Transactional
    public ImportResultResponse importCards(final UUID deckId, final MultipartFile file,
            final ImportCardsRequest request, final UUID userId) {
        Objects.requireNonNull(deckId, MSG_DECK_ID_CANNOT_BE_NULL);
        Objects.requireNonNull(file, "File cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("event={} Importing cards: deckId={}, fileName={}, userId={}",
                LogEvent.START, deckId, file.getOriginalFilename(), userId);

        // Step 1: Validate deck ownership
        final var deck = getDeckWithOwnershipCheck(deckId, userId);
        final var user = getUser(userId);

        // Step 2: Validate file
        validateFile(file);

        // Step 3: Parse file based on format
        final var fileName = file.getOriginalFilename();
        final var extension = getFileExtension(fileName);

        final List<CardRow> cardRows;
        try {
            if ("csv".equalsIgnoreCase(extension)) {
                cardRows = parseCsvFile(file);
            } else if ("xlsx".equalsIgnoreCase(extension)) {
                cardRows = parseXlsxFile(file);
            } else {
                throw new ValidationException(
                        "IMP_001",
                        getMessage("error.import.file.invalid.type"));
            }
        } catch (final IOException e) {
            log.error("event={} Error parsing file: {}", LogEvent.EX_INTERNAL_SERVER, e.getMessage());
            throw new ValidationException(
                    "IMP_002",
                    getMessage("error.import.invalid.format"));
        }

        // Step 4: Validate row count
        if (cardRows.size() > MAX_ROWS) {
            throw new ValidationException(
                    "IMP_003",
                    getMessage("error.import.row.limit.exceeded", cardRows.size(), MAX_ROWS));
        }

        // Step 5: Process rows
        final var policy = (request != null) && (request.getDuplicatePolicy() != null)
                ? request.getDuplicatePolicy()
                : ImportCardsRequest.DuplicatePolicy.SKIP;

        return processImport(deck, user, cardRows, policy);
    }

    // ==================== UC-022: Export Cards ====================

    @Override
    public Resource exportCards(final UUID deckId, final String format, final String scope, final UUID userId) {
        Objects.requireNonNull(deckId, MSG_DECK_ID_CANNOT_BE_NULL);
        Objects.requireNonNull(format, "Format cannot be null");
        Objects.requireNonNull(scope, "Scope cannot be null");

        log.info("event={} Exporting cards: deckId={}, format={}, scope={}, userId={}",
                LogEvent.START, deckId, format, scope, userId);

        // Step 1: Validate deck ownership
        final var deck = getDeckWithOwnershipCheck(deckId, userId);

        // Step 2: Get cards based on scope
        final var cards = getCardsForExport(deck, scope, userId);

        // Step 3: Generate file
        try {
            if ("csv".equalsIgnoreCase(format)) {
                return generateCsvFile(cards, deck);
            }
            if ("xlsx".equalsIgnoreCase(format)) {
                return generateXlsxFile(cards, deck);
            }
            throw new ValidationException(
                    "EXP_001",
                    getMessage("error.import.file.invalid.type"));
        } catch (final IOException e) {
            log.error("event={} Error generating export file: {}", LogEvent.EX_INTERNAL_SERVER, e.getMessage());
            throw new ValidationException(
                    "EXP_002",
                    getMessage("error.internal.server"));
        }
    }

    // ==================== Helper Methods ====================

    private void validateFile(final MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException(
                    "IMP_004",
                    getMessage("error.import.file.required"));
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException(
                    "IMP_005",
                    getMessage("error.import.file.too.large", MAX_FILE_SIZE / (1024 * 1024)));
        }
    }

    private List<CardRow> parseCsvFile(final MultipartFile file) throws IOException {
        final List<CardRow> rows = new ArrayList<>();

        try (var reader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            final var allRows = reader.readAll();

            // Skip header row
            for (var i = 1; i < allRows.size(); i++) {
                final var row = allRows.get(i);
                if (row.length >= 2) {
                    rows.add(new CardRow(i + 1, row[0], row[1]));
                }
            }
        } catch (final CsvException e) {
            throw new IOException("Error parsing CSV file", e);
        }

        return rows;
    }

    private List<CardRow> parseXlsxFile(final MultipartFile file) throws IOException {
        final List<CardRow> rows = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            final var sheet = workbook.getSheetAt(0);

            // Skip header row
            for (var i = 1; i <= sheet.getLastRowNum(); i++) {
                final var row = sheet.getRow(i);
                if ((row != null) && (row.getCell(0) != null) && (row.getCell(1) != null)) {
                    final var front = getCellValue(row.getCell(0));
                    final var back = getCellValue(row.getCell(1));
                    if (StringUtils.isNotBlank(front) || StringUtils.isNotBlank(back)) {
                        rows.add(new CardRow(i + 1, front, back));
                    }
                }
            }
        }

        return rows;
    }

    private String getCellValue(final Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
        case STRING:
            return cell.getStringCellValue();
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            } else {
                return String.valueOf((long) cell.getNumericCellValue());
            }
        case BOOLEAN:
            return String.valueOf(cell.getBooleanCellValue());
        case FORMULA:
            return cell.getCellFormula();
        default:
            return "";
        }
    }

    private ImportResultResponse processImport(
            final Deck deck,
            final User user,
            final List<CardRow> cardRows,
            final ImportCardsRequest.DuplicatePolicy policy) {

        var imported = 0;
        var skipped = 0;
        var failed = 0;
        final List<ImportResultResponse.ImportError> errors = new ArrayList<>();

        for (final CardRow row : cardRows) {
            try {
                // Validate row
                if (StringUtils.isBlank(row.front)) {
                    failed++;
                    errors.add(ImportResultResponse.ImportError.builder()
                            .row(row.rowNumber)
                            .field("front")
                            .message(getMessage("error.import.row.front.empty", row.rowNumber))
                            .build());
                    continue;
                }

                if (StringUtils.isBlank(row.back)) {
                    failed++;
                    errors.add(ImportResultResponse.ImportError.builder()
                            .row(row.rowNumber)
                            .field("back")
                            .message(getMessage("error.import.row.back.empty", row.rowNumber))
                            .build());
                    continue;
                }

                if (row.front.length() > 5000) {
                    failed++;
                    errors.add(ImportResultResponse.ImportError.builder()
                            .row(row.rowNumber)
                            .field("front")
                            .message(getMessage("error.import.row.front.too.long", row.rowNumber, 5000))
                            .build());
                    continue;
                }

                if (row.back.length() > 5000) {
                    failed++;
                    errors.add(ImportResultResponse.ImportError.builder()
                            .row(row.rowNumber)
                            .field("back")
                            .message(getMessage("error.import.row.back.too.long", row.rowNumber, 5000))
                            .build());
                    continue;
                }

                // Check duplicate
                final var duplicate = this.cardRepository.existsByDeckIdAndFrontIgnoreCase(
                        deck.getId(), row.front.trim());

                if (duplicate && (policy == ImportCardsRequest.DuplicatePolicy.SKIP)) {
                    skipped++;
                    continue;
                }

                // Create card
                final var createRequest = CreateCardRequest.builder()
                        .front(row.front.trim())
                        .back(row.back.trim())
                        .build();

                this.cardService.createCard(deck.getId(), createRequest, user.getId());
                imported++;

            } catch (final Exception e) {
                failed++;
                errors.add(ImportResultResponse.ImportError.builder()
                        .row(row.rowNumber)
                        .field("general")
                        .message(e.getMessage())
                        .build());
            }
        }

        log.info("event={} Import completed: imported={}, skipped={}, failed={}, userId={}",
                LogEvent.SUCCESS, imported, skipped, failed, user.getId());

        return ImportResultResponse.builder()
                .imported(imported)
                .skipped(skipped)
                .failed(failed)
                .duplicatePolicy(policy.name())
                .errors(errors.isEmpty() ? null : errors)
                .build();
    }

    private List<Card> getCardsForExport(final Deck deck, final String scope, final UUID userId) {
        final var allCards = this.cardRepository.findByDeckIdAndDeletedAtIsNull(deck.getId());

        if ("DUE_ONLY".equalsIgnoreCase(scope)) {
            final var today = LocalDate.now();
            final Set<UUID> dueCardIds = this.cardBoxPositionRepository.findAll().stream()
                    .filter(pos -> pos.getUser().getId().equals(userId))
                    .filter(pos -> pos.getCard().getDeck().getId().equals(deck.getId()))
                    .filter(pos -> pos.getDueDate().isBefore(today) || pos.getDueDate().isEqual(today))
                    .map(pos -> pos.getCard().getId())
                    .collect(Collectors.toSet());

            return allCards.stream()
                    .filter(card -> dueCardIds.contains(card.getId()))
                    .toList();
        }

        return allCards; // ALL scope
    }

    private Resource generateCsvFile(final List<Card> cards, final Deck deck) throws IOException {
        final var outputStream = new ByteArrayOutputStream();
        final var writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        try (var csvWriter = new CSVWriter(writer)) {
            // Write header
            csvWriter.writeNext(new String[] { "Front", "Back", "Box", "DueDate", "ReviewCount", "Status",
                    "CreatedAt" });

            // Write data rows
            for (final Card card : cards) {
                // Get card box position if exists
                final var positionOpt = this.cardBoxPositionRepository.findAll().stream()
                        .filter(pos -> pos.getCard().getId().equals(card.getId()))
                        .findFirst();

                final var box = positionOpt.map(pos -> String.valueOf(pos.getCurrentBox())).orElse("1");
                final var dueDate = positionOpt.map(pos -> pos.getDueDate().toString()).orElse("");
                final var reviewCount = positionOpt.map(pos -> String.valueOf(pos.getReviewCount())).orElse("0");
                final var status = positionOpt.map(pos -> pos.isNew() ? "NEW" : "REVIEWING").orElse("NEW");
                final var createdAt = card.getCreatedAt() != null
                        ? card.getCreatedAt().toString()
                        : "";

                csvWriter.writeNext(new String[] {
                        card.getFront(),
                        card.getBack(),
                        box,
                        dueDate,
                        reviewCount,
                        status,
                        createdAt
                });
            }
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private Resource generateXlsxFile(final List<Card> cards, final Deck deck) throws IOException {
        final var outputStream = new ByteArrayOutputStream();

        try (Workbook workbook = new XSSFWorkbook()) {
            final var sheet = workbook.createSheet("Cards");

            // Create header row
            final var headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Front");
            headerRow.createCell(1).setCellValue("Back");
            headerRow.createCell(2).setCellValue("Box");
            headerRow.createCell(3).setCellValue("DueDate");
            headerRow.createCell(4).setCellValue("ReviewCount");
            headerRow.createCell(5).setCellValue("Status");
            headerRow.createCell(6).setCellValue("CreatedAt");

            // Write data rows
            var rowNum = 1;
            for (final Card card : cards) {
                final var row = sheet.createRow(rowNum);
                rowNum++;

                // Get card box position if exists
                final var positionOpt = this.cardBoxPositionRepository.findAll().stream()
                        .filter(pos -> pos.getCard().getId().equals(card.getId()))
                        .findFirst();

                row.createCell(0).setCellValue(card.getFront());
                row.createCell(1).setCellValue(card.getBack());
                row.createCell(2).setCellValue(positionOpt.map(CardBoxPosition::getCurrentBox).orElse(1));
                row.createCell(3).setCellValue(
                        positionOpt.map(pos -> pos.getDueDate().toString()).orElse(""));
                row.createCell(4).setCellValue(
                        positionOpt.map(CardBoxPosition::getReviewCount).orElse(0));
                row.createCell(5).setCellValue(
                        positionOpt.map(pos -> pos.isNew() ? "NEW" : "REVIEWING").orElse("NEW"));
                row.createCell(6).setCellValue(
                        card.getCreatedAt() != null ? card.getCreatedAt().toString() : "");
            }

            workbook.write(outputStream);
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private String getFileExtension(final String fileName) {
        if ((fileName == null) || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private Deck getDeckWithOwnershipCheck(final UUID deckId, final UUID userId) {
        return this.deckRepository.findByIdAndUserId(deckId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DECK_002",
                        getMessage("error.deck.not.found", deckId)));
    }

    private User getUser(final UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "USER_001",
                        getMessage("error.user.not.found", userId)));
    }

    /**
     * Internal class for card row data
     */
    private static class CardRow {
        final int rowNumber;
        final String front;
        final String back;

        CardRow(final int rowNumber, final String front, final String back) {
            this.rowNumber = rowNumber;
            this.front = front != null ? front.trim() : "";
            this.back = back != null ? back.trim() : "";
        }
    }
}
