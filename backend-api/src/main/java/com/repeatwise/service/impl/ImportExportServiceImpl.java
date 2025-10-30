package com.repeatwise.service.impl;

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
import com.repeatwise.repository.CardBoxPositionRepository;
import com.repeatwise.repository.CardRepository;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.ICardService;
import com.repeatwise.service.IImportExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
public class ImportExportServiceImpl implements IImportExportService {

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final int MAX_ROWS = 10000;
    private static final int SYNC_THRESHOLD = 5000;

    private final CardRepository cardRepository;
    private final CardBoxPositionRepository cardBoxPositionRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final ICardService cardService;
    private final MessageSource messageSource;

    // ==================== UC-021: Import Cards ====================

    @Override
    @Transactional
    public ImportResultResponse importCards(final UUID deckId, final MultipartFile file,
                                            final ImportCardsRequest request, final UUID userId) {
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(file, "File cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Importing cards: deckId={}, fileName={}, userId={}",
            LogEvent.START, deckId, file.getOriginalFilename(), userId);

        // Step 1: Validate deck ownership
        final Deck deck = getDeckWithOwnershipCheck(deckId, userId);
        final User user = getUser(userId);

        // Step 2: Validate file
        validateFile(file);

        // Step 3: Parse file based on format
        final String fileName = file.getOriginalFilename();
        final String extension = getFileExtension(fileName);

        final List<CardRow> cardRows;
        try {
            if ("csv".equalsIgnoreCase(extension)) {
                cardRows = parseCsvFile(file);
            } else if ("xlsx".equalsIgnoreCase(extension)) {
                cardRows = parseXlsxFile(file);
            } else {
                throw new ValidationException(
                    "IMP_001",
                    getMessage("error.import.file.invalid.type")
                );
            }
        } catch (IOException e) {
            log.error("event={} Error parsing file: {}", LogEvent.EX_INTERNAL_SERVER, e.getMessage());
            throw new ValidationException(
                "IMP_002",
                getMessage("error.import.invalid.format")
            );
        }

        // Step 4: Validate row count
        if (cardRows.size() > MAX_ROWS) {
            throw new ValidationException(
                "IMP_003",
                getMessage("error.import.row.limit.exceeded", cardRows.size(), MAX_ROWS)
            );
        }

        // Step 5: Process rows
        final ImportCardsRequest.DuplicatePolicy policy = request != null && request.getDuplicatePolicy() != null
            ? request.getDuplicatePolicy()
            : ImportCardsRequest.DuplicatePolicy.SKIP;

        return processImport(deck, user, cardRows, policy);
    }

    // ==================== UC-022: Export Cards ====================

    @Override
    public Resource exportCards(final UUID deckId, final String format, final String scope, final UUID userId) {
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(format, "Format cannot be null");
        Objects.requireNonNull(scope, "Scope cannot be null");

        log.info("event={} Exporting cards: deckId={}, format={}, scope={}, userId={}",
            LogEvent.START, deckId, format, scope, userId);

        // Step 1: Validate deck ownership
        final Deck deck = getDeckWithOwnershipCheck(deckId, userId);

        // Step 2: Get cards based on scope
        final List<Card> cards = getCardsForExport(deck, scope, userId);

        // Step 3: Generate file
        try {
            if ("csv".equalsIgnoreCase(format)) {
                return generateCsvFile(cards, deck);
            } else if ("xlsx".equalsIgnoreCase(format)) {
                return generateXlsxFile(cards, deck);
            } else {
                throw new ValidationException(
                    "EXP_001",
                    getMessage("error.import.file.invalid.type")
                );
            }
        } catch (IOException e) {
            log.error("event={} Error generating export file: {}", LogEvent.EX_INTERNAL_SERVER, e.getMessage());
            throw new ValidationException(
                "EXP_002",
                getMessage("error.internal.server")
            );
        }
    }

    // ==================== Helper Methods ====================

    private void validateFile(final MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException(
                "IMP_004",
                getMessage("error.import.file.required")
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException(
                "IMP_005",
                getMessage("error.import.file.too.large", MAX_FILE_SIZE / (1024 * 1024))
            );
        }
    }

    private List<CardRow> parseCsvFile(final MultipartFile file) throws IOException {
        final List<CardRow> rows = new ArrayList<>();

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            final List<String[]> allRows = reader.readAll();

            // Skip header row
            for (int i = 1; i < allRows.size(); i++) {
                final String[] row = allRows.get(i);
                if (row.length >= 2) {
                    rows.add(new CardRow(i + 1, row[0], row[1]));
                }
            }
        } catch (CsvException e) {
            throw new IOException("Error parsing CSV file", e);
        }

        return rows;
    }

    private List<CardRow> parseXlsxFile(final MultipartFile file) throws IOException {
        final List<CardRow> rows = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            final Sheet sheet = workbook.getSheetAt(0);

            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                final Row row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null && row.getCell(1) != null) {
                    final String front = getCellValue(row.getCell(0));
                    final String back = getCellValue(row.getCell(1));
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

        int imported = 0;
        int skipped = 0;
        int failed = 0;
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
                final boolean duplicate = cardRepository.existsByDeckIdAndFrontIgnoreCase(
                    deck.getId(), row.front.trim());

                if (duplicate && policy == ImportCardsRequest.DuplicatePolicy.SKIP) {
                    skipped++;
                    continue;
                }

                // Create card
                final CreateCardRequest createRequest = CreateCardRequest.builder()
                    .front(row.front.trim())
                    .back(row.back.trim())
                    .build();

                cardService.createCard(deck.getId(), createRequest, user.getId());
                imported++;

            } catch (Exception e) {
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
        final List<Card> allCards = cardRepository.findByDeckIdAndDeletedAtIsNull(deck.getId());

        if ("DUE_ONLY".equalsIgnoreCase(scope)) {
            final LocalDate today = LocalDate.now();
            final Set<UUID> dueCardIds = cardBoxPositionRepository.findAll().stream()
                .filter(pos -> pos.getUser().getId().equals(userId))
                .filter(pos -> pos.getCard().getDeck().getId().equals(deck.getId()))
                .filter(pos -> pos.getDueDate().isBefore(today) || pos.getDueDate().isEqual(today))
                .map(pos -> pos.getCard().getId())
                .collect(Collectors.toSet());

            return allCards.stream()
                .filter(card -> dueCardIds.contains(card.getId()))
                .collect(Collectors.toList());
        }

        return allCards; // ALL scope
    }

    private Resource generateCsvFile(final List<Card> cards, final Deck deck) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            // Write header
            csvWriter.writeNext(new String[]{"Front", "Back", "Box", "DueDate", "ReviewCount", "Status", "CreatedAt"});

            // Write data rows
            for (final Card card : cards) {
                // Get card box position if exists
                final Optional<CardBoxPosition> positionOpt = cardBoxPositionRepository.findAll().stream()
                    .filter(pos -> pos.getCard().getId().equals(card.getId()))
                    .findFirst();

                final String box = positionOpt.map(pos -> String.valueOf(pos.getCurrentBox())).orElse("1");
                final String dueDate = positionOpt.map(pos -> pos.getDueDate().toString()).orElse("");
                final String reviewCount = positionOpt.map(pos -> String.valueOf(pos.getReviewCount())).orElse("0");
                final String status = positionOpt.map(pos -> pos.isNew() ? "NEW" : "REVIEWING").orElse("NEW");
                final String createdAt = card.getCreatedAt() != null
                    ? card.getCreatedAt().toString()
                    : "";

                csvWriter.writeNext(new String[]{
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
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (Workbook workbook = new XSSFWorkbook()) {
            final Sheet sheet = workbook.createSheet("Cards");

            // Create header row
            final Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Front");
            headerRow.createCell(1).setCellValue("Back");
            headerRow.createCell(2).setCellValue("Box");
            headerRow.createCell(3).setCellValue("DueDate");
            headerRow.createCell(4).setCellValue("ReviewCount");
            headerRow.createCell(5).setCellValue("Status");
            headerRow.createCell(6).setCellValue("CreatedAt");

            // Write data rows
            int rowNum = 1;
            for (final Card card : cards) {
                final Row row = sheet.createRow(rowNum++);

                // Get card box position if exists
                final Optional<CardBoxPosition> positionOpt = cardBoxPositionRepository.findAll().stream()
                    .filter(pos -> pos.getCard().getId().equals(card.getId()))
                    .findFirst();

                row.createCell(0).setCellValue(card.getFront());
                row.createCell(1).setCellValue(card.getBack());
                row.createCell(2).setCellValue(positionOpt.map(pos -> pos.getCurrentBox()).orElse(1));
                row.createCell(3).setCellValue(
                    positionOpt.map(pos -> pos.getDueDate().toString()).orElse(""));
                row.createCell(4).setCellValue(
                    positionOpt.map(pos -> pos.getReviewCount()).orElse(0));
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
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private Deck getDeckWithOwnershipCheck(final UUID deckId, final UUID userId) {
        return deckRepository.findByIdAndUserId(deckId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "DECK_002",
                getMessage("error.deck.not.found", deckId)
            ));
    }

    private User getUser(final UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "USER_001",
                getMessage("error.user.not.found", userId)
            ));
    }

    private String getMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
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

