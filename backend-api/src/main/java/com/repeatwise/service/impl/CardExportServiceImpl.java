package com.repeatwise.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.repeatwise.config.properties.AppProperties;
import com.repeatwise.entity.AsyncJob;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.entity.Deck;
import com.repeatwise.enums.AsyncJobStatus;
import com.repeatwise.enums.AsyncJobType;
import com.repeatwise.enums.ExportFormat;
import com.repeatwise.enums.ExportScope;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.repository.AsyncJobRepository;
import com.repeatwise.repository.CardRepository;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.service.CardExportService;
import com.repeatwise.service.FileStorageService;

import lombok.extern.slf4j.Slf4j;

/**
 * Service export thẻ (UC-022).
 */
@Service
@Slf4j
public class CardExportServiceImpl implements CardExportService {

    private static final String[] CSV_HEADERS = {
            "Front", "Back", "Box", "DueDate", "ReviewCount", "Status", "CreatedAt"
    };

    private static final DateTimeFormatter FILE_TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final AsyncJobRepository asyncJobRepository;
    private final FileStorageService fileStorageService;
    private final MessageSource messageSource;
    private final AppProperties appProperties;
    private final TransactionTemplate transactionTemplate;
    private final Executor jobExecutor;

    public CardExportServiceImpl(
            DeckRepository deckRepository,
            CardRepository cardRepository,
            AsyncJobRepository asyncJobRepository,
            FileStorageService fileStorageService,
            MessageSource messageSource,
            AppProperties appProperties,
            PlatformTransactionManager transactionManager,
            @Qualifier("jobTaskExecutor") Executor jobExecutor) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.asyncJobRepository = asyncJobRepository;
        this.fileStorageService = fileStorageService;
        this.messageSource = messageSource;
        this.appProperties = appProperties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.jobExecutor = jobExecutor;
    }

    @Override
    public ExportResult exportSync(UUID deckId, UUID userId, ExportFormat format, ExportScope scope) {
        final var deck = getDeckOrThrow(deckId, userId);
        final var limits = this.appProperties.getLimits();
        final var total = countCards(deckId, userId, scope);
        validateCounts(total, limits);

        final var cards = loadCards(deckId, userId, scope);
        final byte[] data = switch (format) {
        case CSV -> generateCsv(cards, userId);
        case XLSX -> generateXlsx(cards, userId);
        };
        final var filename = buildFilename(deck.getName(), format);
        final var contentType = format.getContentType();
        return new ExportResult(
                new ByteArrayResource(data),
                filename,
                contentType,
                data.length,
                null,
                null,
                total);
    }

    @Override
    public AsyncJob startAsyncExport(UUID deckId, UUID userId, ExportFormat format, ExportScope scope) {
        final var locale = LocaleContextHolder.getLocale();
        final var limits = this.appProperties.getLimits();
        final var total = countCards(deckId, userId, scope);
        validateCounts(total, limits);

        if (total == 0L) {
            throw new RepeatWiseException(RepeatWiseError.RESOURCE_NOT_FOUND, message("export.no.cards"));
        }

        if (total <= limits.getExportSyncThreshold()) {
            throw new RepeatWiseException(RepeatWiseError.EXPORT_INVALID_FORMAT,
                    "Use synchronous export for small datasets");
        }

        final var job = createExportJob(deckId, userId, format, scope, total);
        submitAsyncExport(job.getId(), locale);
        return job;
    }

    @Override
    public Resource loadExportFile(UUID jobId, UUID userId) {
        final var job = this.asyncJobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
        final var path = job.getResultPath();
        if (path == null) {
            throw new RepeatWiseException(RepeatWiseError.EXPORT_FILE_EXPIRED);
        }
        return this.fileStorageService.loadAsResource(Path.of(path));
    }

    @Override
    public ExportResponse exportCards(UUID deckId, UUID userId, ExportFormat format, ExportScope scope) {
        final var limits = this.appProperties.getLimits();
        final var total = countCards(deckId, userId, scope);
        validateCounts(total, limits);

        if (total == 0) {
            final var result = exportSync(deckId, userId, format, scope);
            return new ExportResponse(result, null);
        }

        if (total > limits.getExportSyncThreshold()) {
            final var job = startAsyncExport(deckId, userId, format, scope);
            return new ExportResponse(null, job);
        }

        final var result = exportSync(deckId, userId, format, scope);
        return new ExportResponse(result, null);
    }

    private void submitAsyncExport(UUID jobId, Locale locale) {
        this.jobExecutor.execute(() -> runAsyncExport(jobId, locale));
    }

    private void runAsyncExport(UUID jobId, Locale locale) {
        LocaleContextHolder.setLocale(locale);
        try {
            this.transactionTemplate.executeWithoutResult(status -> {
                final var job = this.asyncJobRepository.findById(jobId)
                        .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
                job.setStatus(AsyncJobStatus.RUNNING);
                job.setStartedAt(LocalDateTime.now());
                job.setMessage(message("export.running"));
                this.asyncJobRepository.save(job);

                final var cards = loadCards(job.getDeckId(), job.getUserId(), ExportScope.valueOf(job.getExportScope()));
                final var format = ExportFormat.valueOf(job.getExportFormat());
                final var path = this.fileStorageService.resolveExportFile(jobId, format);
                writeFile(format, cards, job.getUserId(), path);

                job.setStatus(AsyncJobStatus.COMPLETED);
                job.setCompletedAt(LocalDateTime.now());
                job.setResultPath(path.toString());
                job.setSuccessCount(cards.size());
                job.setProcessedRows(cards.size());
                job.setMessage(message("export.completed"));
                this.asyncJobRepository.save(job);
            });
            log.info("Export job {} completed", jobId);
        } catch (RepeatWiseException ex) {
            markExportFailed(jobId, ex.getError(), ex.getMessageArgs());
        } catch (Exception ex) {
            log.error("Export job {} failed", jobId, ex);
            markExportFailed(jobId, RepeatWiseError.INTERNAL_SERVER_ERROR);
        }
    }

    private void markExportFailed(UUID jobId, RepeatWiseError error, Object... args) {
        this.transactionTemplate.executeWithoutResult(status -> {
            final var job = this.asyncJobRepository.findById(jobId)
                    .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
            job.setStatus(AsyncJobStatus.FAILED);
            job.setCompletedAt(LocalDateTime.now());
            job.setMessage(message(error.getMessageKey(), args));
            this.asyncJobRepository.save(job);
        });
    }

    private void validateCounts(long total, AppProperties.Limits limits) {
        if (total > limits.getMaxExportRows()) {
            throw new RepeatWiseException(RepeatWiseError.EXPORT_TOO_MANY_CARDS, total, limits.getMaxExportRows());
        }
    }

    private long countCards(UUID deckId, UUID userId, ExportScope scope) {
        return scope == ExportScope.DUE_ONLY
                ? this.cardRepository.countDueCardsByDeckIdAndUserId(deckId, userId)
                : this.cardRepository.countActiveByDeckIdAndUserId(deckId, userId);
    }

    private List<Card> loadCards(UUID deckId, UUID userId, ExportScope scope) {
        final var cards = this.cardRepository.findActiveWithPositionsByDeckIdAndUserId(deckId, userId);
        if (scope == ExportScope.ALL) {
            return cards;
        }
        final var today = LocalDate.now();
        return cards.stream()
                .filter(card -> positionFor(card, userId)
                        .map(pos -> !pos.getDueDate().isAfter(today))
                        .orElse(false))
                .toList();
    }

    private byte[] generateCsv(List<Card> cards, UUID userId) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                        .setHeader(CSV_HEADERS)
                        .build())) {
            for (final Card card : cards) {
                final var position = positionFor(card, userId).orElse(null);
                printer.printRecord(
                        card.getFront(),
                        card.getBack(),
                        position != null ? position.getCurrentBox() : "",
                        position != null ? position.getDueDate() : "",
                        position != null ? position.getReviewCount() : "",
                        position != null ? statusOf(position) : "",
                        card.getCreatedAt());
            }
            printer.flush();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RepeatWiseException(RepeatWiseError.EXPORT_GENERATION_FAILED, ex);
        }
    }

    private byte[] generateXlsx(List<Card> cards, UUID userId) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final var sheet = workbook.createSheet("Cards");
            final var header = sheet.createRow(0);
            for (int i = 0; i < CSV_HEADERS.length; i++) {
                final Cell cell = header.createCell(i);
                cell.setCellValue(CSV_HEADERS[i]);
            }
            final CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd"));

            var rowIndex = 1;
            for (final Card card : cards) {
                final Row row = sheet.createRow(rowIndex++);
                final var position = positionFor(card, userId).orElse(null);
                row.createCell(0).setCellValue(card.getFront());
                row.createCell(1).setCellValue(card.getBack());
                if (position != null) {
                    row.createCell(2).setCellValue(position.getCurrentBox());
                    final var dueCell = row.createCell(3);
                    dueCell.setCellValue(position.getDueDate());
                    dueCell.setCellStyle(dateStyle);
                    row.createCell(4).setCellValue(position.getReviewCount());
                    row.createCell(5).setCellValue(statusOf(position));
                }
                row.createCell(6).setCellValue(card.getCreatedAt().toString());
            }
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RepeatWiseException(RepeatWiseError.EXPORT_GENERATION_FAILED, ex);
        }
    }

    private void writeFile(ExportFormat format, List<Card> cards, UUID userId, Path path) {
        final byte[] data = switch (format) {
        case CSV -> generateCsv(cards, userId);
        case XLSX -> generateXlsx(cards, userId);
        };
        try {
            Files.write(path, data);
        } catch (IOException ex) {
            throw new RepeatWiseException(RepeatWiseError.EXPORT_GENERATION_FAILED, ex);
        }
    }

    private AsyncJob createExportJob(UUID deckId, UUID userId, ExportFormat format, ExportScope scope, long total) {
        final var job = new AsyncJob();
        job.setDeckId(deckId);
        job.setUserId(userId);
        job.setJobType(AsyncJobType.EXPORT_CARDS);
        job.setStatus(AsyncJobStatus.PENDING);
        job.setExportFormat(format.name());
        job.setExportScope(scope.name());
        job.setTotalRows((int) total);
        job.setProcessedRows(0);
        job.setMessage(message("export.pending"));
        job.setStartedAt(LocalDateTime.now());
        return this.asyncJobRepository.save(job);
    }

    private Deck getDeckOrThrow(UUID deckId, UUID userId) {
        return this.deckRepository.findByIdAndUserId(deckId, userId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.DECK_NOT_FOUND, deckId));
    }

    private Optional<CardBoxPosition> positionFor(Card card, UUID userId) {
        if (card.getCardBoxPositions() == null) {
            return Optional.empty();
        }
        return card.getCardBoxPositions().stream()
                .filter(pos -> pos.getDeletedAt() == null && pos.getUser().getId().equals(userId))
                .findFirst();
    }

    private String statusOf(CardBoxPosition position) {
        if (position.getReviewCount() == null || position.getReviewCount() == 0) {
            return "NEW";
        }
        return position.getCurrentBox() >= 5 ? "MATURE" : "LEARNING";
    }

    private String buildFilename(String deckName, ExportFormat format) {
        final var sanitized = deckName.replaceAll("[^a-zA-Z0-9-_]", "_");
        final var timestamp = LocalDateTime.now().format(FILE_TS_FORMAT);
        return sanitized + "-" + timestamp + "." + format.getExtension();
    }

    private String message(String key, Object... args) {
        return this.messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}

