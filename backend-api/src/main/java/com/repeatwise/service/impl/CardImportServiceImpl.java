package com.repeatwise.service.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.repeatwise.config.properties.AppProperties;
import com.repeatwise.config.properties.AppProperties.Limits;
import com.repeatwise.csv.CsvHelper;
import com.repeatwise.csv.CsvRecord;
import com.repeatwise.csv.CsvSchema;
import com.repeatwise.csv.GenericCsvService;
import com.repeatwise.csv.CsvUtils;
import com.repeatwise.entity.AsyncJob;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.entity.Deck;
import com.repeatwise.enums.AsyncJobStatus;
import com.repeatwise.enums.AsyncJobType;
import com.repeatwise.enums.DuplicateHandlingPolicy;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.repository.AsyncJobRepository;
import com.repeatwise.repository.CardBoxPositionRepository;
import com.repeatwise.repository.CardRepository;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.service.CardImportService;
import com.repeatwise.service.FileStorageService;
import com.repeatwise.util.TextUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Triển khai import thẻ (UC-021).
 */
@Service
@Slf4j
public class CardImportServiceImpl implements CardImportService {

    private static final String HEADER_FRONT = "front";
    private static final String HEADER_BACK = "back";
    private static final int MAX_CONTENT_LENGTH = 5000;

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final CardBoxPositionRepository cardBoxPositionRepository;
    private final AsyncJobRepository asyncJobRepository;
    private final FileStorageService fileStorageService;
    private final MessageSource messageSource;
    private final AppProperties appProperties;
    private final TransactionTemplate transactionTemplate;
    private final Executor jobExecutor;
    private final GenericCsvService genericCsvService;

    @Override
    public ImportResponse importCards(UUID deckId, UUID userId, MultipartFile file, DuplicateHandlingPolicy policy) {
        final var locale = LocaleContextHolder.getLocale();
        final var parsed = parseFile(file);
        final var limits = this.appProperties.getLimits();
        validateFileStats(parsed.totalRows(), file.getSize(), limits);

        if (parsed.totalRows() > limits.getImportSyncThreshold()) {
            final var job = startAsyncInternal(deckId, userId, file, policy, parsed, locale, limits);
            return new ImportResponse(null, job);
        }

        final var result = runSyncImport(deckId, userId, policy, parsed, locale, limits);
        final var job = this.asyncJobRepository.findById(result.jobId()).orElse(null);
        return new ImportResponse(result, job);
    }

    public CardImportServiceImpl(
            DeckRepository deckRepository,
            CardRepository cardRepository,
            CardBoxPositionRepository cardBoxPositionRepository,
            AsyncJobRepository asyncJobRepository,
            FileStorageService fileStorageService,
            MessageSource messageSource,
            AppProperties appProperties,
            org.springframework.transaction.PlatformTransactionManager transactionManager,
            @org.springframework.beans.factory.annotation.Qualifier("jobTaskExecutor") Executor jobExecutor,
            GenericCsvService genericCsvService) {
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.cardBoxPositionRepository = cardBoxPositionRepository;
        this.asyncJobRepository = asyncJobRepository;
        this.fileStorageService = fileStorageService;
        this.messageSource = messageSource;
        this.appProperties = appProperties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.jobExecutor = jobExecutor;
        this.genericCsvService = genericCsvService;
    }

    @Override
    public ImportResult importSync(UUID deckId, UUID userId, MultipartFile file, DuplicateHandlingPolicy policy) {
        final var locale = LocaleContextHolder.getLocale();
        final var parsed = parseFile(file);
        final var limits = this.appProperties.getLimits();
        validateFileStats(parsed.totalRows(), file.getSize(), limits);
        if (parsed.totalRows() > limits.getImportSyncThreshold()) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_TOO_MANY_ROWS, parsed.totalRows(),
                    limits.getImportSyncThreshold());
        }
        return runSyncImport(deckId, userId, policy, parsed, locale, limits);
    }

    @Override
    public AsyncJob startAsyncImport(UUID deckId, UUID userId, MultipartFile file, DuplicateHandlingPolicy policy) {
        final var locale = LocaleContextHolder.getLocale();
        final var parsed = parseFile(file);
        final var limits = this.appProperties.getLimits();
        validateFileStats(parsed.totalRows(), file.getSize(), limits);
        if (parsed.totalRows() <= limits.getImportSyncThreshold()) {
            final var result = runSyncImport(deckId, userId, policy, parsed, locale, limits);
            return this.asyncJobRepository.findById(result.jobId()).orElse(null);
        }
        return startAsyncInternal(deckId, userId, file, policy, parsed, locale, limits);
    }

    @Override
    public org.springframework.core.io.Resource loadErrorReport(UUID jobId, UUID userId) {
        final var job = this.asyncJobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
        final var path = job.getErrorReportPath();
        if (path == null) {
            throw new RepeatWiseException(RepeatWiseError.RESOURCE_NOT_FOUND, jobId);
        }
        return this.fileStorageService.loadAsResource(Path.of(path));
    }

    @Override
    public void deleteFile(Path path) {
        this.fileStorageService.deleteQuietly(path);
    }

    private void submitAsyncImport(UUID jobId, Locale locale) {
        this.jobExecutor.execute(() -> runAsyncImport(jobId, locale));
    }

    private ImportResult runSyncImport(UUID deckId, UUID userId, DuplicateHandlingPolicy policy, ParsedFile parsed,
            Locale locale, Limits limits) {
        final var deck = getDeckOrThrow(deckId, userId);
        final var job = createJob(deckId, userId, policy, parsed.totalRows(), AsyncJobStatus.RUNNING);
        final var result = executeImport(deck, userId, policy, parsed.rows(), job, locale, limits);
        finalizeJob(job, result, locale);
        maybeWriteErrorReport(job, result.errors(), locale);
        return new ImportResult(
                result.imported(),
                result.skipped(),
                result.failed(),
                parsed.totalRows(),
                policy,
                Optional.ofNullable(job.getErrorReportPath()).map(Path::of).orElse(null),
                job.getId());
    }

    private AsyncJob startAsyncInternal(UUID deckId, UUID userId, MultipartFile file, DuplicateHandlingPolicy policy,
            ParsedFile parsed, Locale locale, Limits limits) {
        final var job = createJob(deckId, userId, policy, parsed.totalRows(), AsyncJobStatus.PENDING);
        final var payloadPath = this.fileStorageService.saveImportUpload(job.getId(), file);
        job.setPayloadPath(payloadPath.toString());
        job.setMessage(buildMessage(locale, "import.pending"));
        this.asyncJobRepository.save(job);

        submitAsyncImport(job.getId(), locale);
        return job;
    }

    private void runAsyncImport(UUID jobId, Locale locale) {
        LocaleContextHolder.setLocale(locale);
        this.transactionTemplate.executeWithoutResult(status -> {
            final var job = this.asyncJobRepository.findById(jobId)
                    .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
            job.setStatus(AsyncJobStatus.RUNNING);
            job.setStartedAt(LocalDateTime.now());
            job.setMessage(buildMessage(locale, "import.running", 0, Optional.ofNullable(job.getTotalRows()).orElse(0)));
            this.asyncJobRepository.save(job);
        });

        ImportComputation result = null;
        RepeatWiseError errorToRaise = null;
        try {
            result = doAsyncImport(jobId, locale);
        } catch (RepeatWiseException ex) {
            errorToRaise = ex.getError();
            if (ex.getError() != RepeatWiseError.JOB_TIMEOUT) {
                log.error("Import job {} thất bại: {}", jobId, ex.getMessage(), ex);
                markJobFailed(jobId, ex.getError(), ex.getMessageArgs());
            } else {
                log.warn("Import job {} hết thời gian xử lý", jobId);
            }
        } catch (Exception ex) {
            errorToRaise = RepeatWiseError.INTERNAL_SERVER_ERROR;
            log.error("Import job {} lỗi hệ thống", jobId, ex);
            markJobFailed(jobId, RepeatWiseError.INTERNAL_SERVER_ERROR);
        }

        if (result != null) {
            final var computation = result;
            this.transactionTemplate.executeWithoutResult(status -> {
                final var job = this.asyncJobRepository.findById(jobId)
                        .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
                job.setStatus(AsyncJobStatus.COMPLETED);
                job.setCompletedAt(LocalDateTime.now());
                job.setSuccessCount(computation.imported());
                job.setSkippedCount(computation.skipped());
                job.setFailedCount(computation.failed());
                job.setProcessedRows(computation.totalRows());
                job.setMessage(buildMessage(locale, "success.card.imported", computation.imported()));
                job.setErrorReportPath(computation.errorReportPath() != null ? computation.errorReportPath().toString() : null);
                this.asyncJobRepository.save(job);
            });
        } else if (errorToRaise == RepeatWiseError.JOB_TIMEOUT) {
            markJobTimeout(jobId);
        }
    }

    private ImportComputation doAsyncImport(UUID jobId, Locale locale) {
        final var limits = this.appProperties.getLimits();
        return this.transactionTemplate.execute(status -> {
            final var job = this.asyncJobRepository.findById(jobId)
                    .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
            final var deck = getDeckOrThrow(job.getDeckId(), job.getUserId());
            final var policy = DuplicateHandlingPolicy.valueOf(job.getDuplicatePolicy());
            final var rows = readRowsFromPayload(job);
            final var result = executeImport(deck, job.getUserId(), policy, rows, job, locale, limits);
            maybeWriteErrorReport(job, result.errors(), locale);
            return new ImportComputation(result.imported(), result.skipped(), result.failed(), rows.size(),
                    Optional.ofNullable(job.getErrorReportPath()).map(Path::of).orElse(null));
        });
    }

    private List<CsvRecord<ImportRow>> readRowsFromPayload(AsyncJob job) {
        final var payloadPath = job.getPayloadPath();
        if (payloadPath == null) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_INVALID_FORMAT);
        }
        final var path = Path.of(payloadPath);
        if (!Files.exists(path)) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_INVALID_FORMAT);
        }
        try (InputStream inputStream = Files.newInputStream(path)) {
            final var extension = StringUtils.substringAfterLast(path.getFileName().toString(), ".").toLowerCase(Locale.ROOT);
            if ("csv".equals(extension)) {
                return parseCsv(inputStream);
            }
            if ("xlsx".equals(extension)) {
                return parseXlsx(inputStream);
            }
            throw new RepeatWiseException(RepeatWiseError.IMPORT_INVALID_FORMAT);
        } catch (IOException ex) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_INVALID_FORMAT, ex);
        }
    }

    private ImportProcessingResult executeImport(
            Deck deck,
            UUID userId,
            DuplicateHandlingPolicy policy,
            List<CsvRecord<ImportRow>> records,
            AsyncJob job,
            Locale locale,
            Limits limits) {
        final var start = LocalDateTime.now();
        final var existingCards = loadExistingCards(deck.getId(), userId);
        final var keyToCard = buildCardIndex(existingCards);
        final var cardsToCreate = new ArrayList<Card>();
        final var cardsToUpdate = new ArrayList<Card>();
        final var errors = new ArrayList<RowError>();
        final var updatedIds = new HashSet<UUID>();
        final var deckMaxCards = limits.getMaxCardsPerDeck();
        final long currentCount = this.cardRepository.countActiveByDeckIdAndUserId(deck.getId(), userId);

        var imported = 0;
        var skipped = 0;
        var failed = 0;
        var processed = 0;

        for (final CsvRecord<ImportRow> record : records) {
            processed++;
            final int rowNumber = record.rowNumber();
            final var check = validateRow(record.data(), rowNumber);
            if (check.isInvalid()) {
                failed++;
                errors.add(check.error());
                continue;
            }
            if (check.shouldSkip()) {
                skipped++;
                continue;
            }
            final var key = buildKey(check.front(), check.back());
            if (keyToCard.containsKey(key)) {
                final var existing = keyToCard.get(key);
                switch (policy) {
                case SKIP -> skipped++;
                case REPLACE -> {
                    if (existing.getDeletedAt() != null) {
                        skipped++;
                        break;
                    }
                    if (updatedIds.add(existing.getId())) {
                        existing.setFront(check.front());
                        existing.setBack(check.back());
                        existing.setUpdatedAt(LocalDateTime.now());
                        cardsToUpdate.add(existing);
                    }
                    imported++;
                }
                case KEEP_BOTH -> {
                    if (wouldExceedCapacity(currentCount, cardsToCreate.size(), deckMaxCards)) {
                        failed++;
                        errors.add(new RowError(rowNumber, "error.import.deck.capacity.exceeded",
                                new Object[] { deckMaxCards }));
                        break;
                    }
                    final var newCard = buildNewCard(deck, check.front(), check.back());
                    cardsToCreate.add(newCard);
                    if (policy != DuplicateHandlingPolicy.KEEP_BOTH) {
                        keyToCard.putIfAbsent(key, newCard);
                    }
                    imported++;
                }
                default -> throw new IllegalStateException("Unsupported policy " + policy);
                }
            } else {
                if (wouldExceedCapacity(currentCount, cardsToCreate.size(), deckMaxCards)) {
                    failed++;
                    errors.add(new RowError(rowNumber, "error.import.deck.capacity.exceeded",
                            new Object[] { deckMaxCards }));
                    continue;
                }
                final var card = buildNewCard(deck, check.front(), check.back());
                cardsToCreate.add(card);
                if (policy != DuplicateHandlingPolicy.KEEP_BOTH) {
                    keyToCard.put(key, card);
                }
                imported++;
            }
            if (job != null && shouldUpdateProgress(processed, limits.getImportBatchSize())) {
                updateJobProgress(job.getId(), processed, imported, skipped, failed, records.size(), locale);
            }
            if (Duration.between(start, LocalDateTime.now()).toMinutes() >= limits.getAsyncJobTimeoutMinutes()) {
                markJobTimeout(job != null ? job.getId() : null);
                throw new RepeatWiseException(RepeatWiseError.JOB_TIMEOUT);
            }
        }

        persistChanges(deck, cardsToCreate, cardsToUpdate);
        return new ImportProcessingResult(imported, skipped, failed, errors);
    }

    private void persistChanges(Deck deck, List<Card> cardsToCreate, List<Card> cardsToUpdate) {
        if (!cardsToUpdate.isEmpty()) {
            this.cardRepository.saveAll(cardsToUpdate);
        }
        if (!cardsToCreate.isEmpty()) {
            final var savedCards = this.cardRepository.saveAll(cardsToCreate);
            final var positions = new ArrayList<CardBoxPosition>(savedCards.size());
            final var owner = deck.getUser();
            for (final Card card : savedCards) {
                positions.add(CardBoxPosition.createNew(card, owner));
            }
            this.cardBoxPositionRepository.saveAll(positions);
        }
    }

    private boolean shouldUpdateProgress(int processed, int batch) {
        return processed % Math.max(1, batch / 2) == 0;
    }

    private boolean wouldExceedCapacity(long current, int pendingCreates, int maxCards) {
        return (current + pendingCreates + 1L) > maxCards;
    }

    private Card buildNewCard(Deck deck, String front, String back) {
        final var card = new Card();
        card.setDeck(deck);
        card.setFront(front);
        card.setBack(back);
        return card;
    }

    private RowValidation validateRow(ImportRow row, int rowNumber) {
        final var front = TextUtils.trimToNull(row.front());
        final var back = TextUtils.trimToNull(row.back());
        if (front == null && back == null) {
            return RowValidation.skip();
        }
        if (front == null) {
            return RowValidation.invalid(new RowError(rowNumber, "error.import.row.front.empty", new Object[] { rowNumber }));
        }
        if (back == null) {
            return RowValidation.invalid(new RowError(rowNumber, "error.import.row.back.empty", new Object[] { rowNumber }));
        }
        if (front.length() > MAX_CONTENT_LENGTH) {
            return RowValidation.invalid(new RowError(rowNumber, "error.import.row.front.too.long",
                    new Object[] { rowNumber, MAX_CONTENT_LENGTH }));
        }
        if (back.length() > MAX_CONTENT_LENGTH) {
            return RowValidation.invalid(new RowError(rowNumber, "error.import.row.back.too.long",
                    new Object[] { rowNumber, MAX_CONTENT_LENGTH }));
        }
        return RowValidation.valid(front, back);
    }

    private void updateJobProgress(UUID jobId, int processed, int imported, int skipped, int failed, int total, Locale locale) {
        this.transactionTemplate.executeWithoutResult(status -> {
            final var job = this.asyncJobRepository.findById(jobId)
                    .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
            job.setProcessedRows(processed);
            job.setSuccessCount(imported);
            job.setSkippedCount(skipped);
            job.setFailedCount(failed);
            job.setMessage(buildMessage(locale, "import.running", processed, total));
            this.asyncJobRepository.save(job);
        });
    }

    private void markJobFailed(UUID jobId, RepeatWiseError error, Object... args) {
        this.transactionTemplate.executeWithoutResult(status -> {
            final var job = this.asyncJobRepository.findById(jobId)
                    .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
            job.setStatus(AsyncJobStatus.FAILED);
            job.setCompletedAt(LocalDateTime.now());
            job.setMessage(buildMessage(LocaleContextHolder.getLocale(), error.getMessageKey(), args));
            this.asyncJobRepository.save(job);
        });
    }

    private void markJobTimeout(UUID jobId) {
        if (jobId == null) {
            return;
        }
        this.transactionTemplate.executeWithoutResult(status -> {
            final var job = this.asyncJobRepository.findById(jobId)
                    .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.JOB_NOT_FOUND, jobId));
            job.setStatus(AsyncJobStatus.TIMEOUT);
            job.setCompletedAt(LocalDateTime.now());
            job.setMessage(buildMessage(LocaleContextHolder.getLocale(), "error.job.timeout"));
            this.asyncJobRepository.save(job);
        });
    }

    private void finalizeJob(AsyncJob job, ImportProcessingResult result, Locale locale) {
        job.setStatus(AsyncJobStatus.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());
        job.setProcessedRows(result.imported() + result.skipped() + result.failed());
        job.setSuccessCount(result.imported());
        job.setSkippedCount(result.skipped());
        job.setFailedCount(result.failed());
        job.setMessage(buildMessage(locale, "success.card.imported", result.imported()));
        this.asyncJobRepository.save(job);
    }

    private AsyncJob createJob(UUID deckId, UUID userId, DuplicateHandlingPolicy policy, int totalRows, AsyncJobStatus status) {
        final var job = new AsyncJob();
        job.setDeckId(deckId);
        job.setUserId(userId);
        job.setJobType(AsyncJobType.IMPORT_CARDS);
        job.setStatus(status);
        job.setDuplicatePolicy(policy.name());
        job.setTotalRows(totalRows);
        job.setProcessedRows(0);
        job.setSuccessCount(0);
        job.setSkippedCount(0);
        job.setFailedCount(0);
        job.setStartedAt(LocalDateTime.now());
        return this.asyncJobRepository.save(job);
    }

    private void maybeWriteErrorReport(AsyncJob job, List<RowError> errors, Locale locale) {
        if (errors.isEmpty()) {
            return;
        }
        final var path = this.fileStorageService.resolveImportErrorReport(job.getId());
        try (var writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
                var printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                        .setHeader("Row", "Message")
                        .build())) {
            for (final RowError error : errors) {
                final var message = buildMessage(locale, error.messageKey(), error.args());
                printer.printRecord(error.rowNumber(), message);
            }
            printer.flush();
            job.setErrorReportPath(path.toString());
            this.asyncJobRepository.save(job);
        } catch (IOException ex) {
            throw new RepeatWiseException(RepeatWiseError.INTERNAL_SERVER_ERROR, ex);
        }
    }

    private String buildMessage(Locale locale, String key, Object... args) {
        return this.messageSource.getMessage(key, args, locale);
    }

    private Deck getDeckOrThrow(UUID deckId, UUID userId) {
        return this.deckRepository.findByIdAndUserId(deckId, userId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.DECK_NOT_FOUND, deckId));
    }

    private List<Card> loadExistingCards(UUID deckId, UUID userId) {
        try {
            return this.cardRepository.findActiveByDeckIdAndUserId(deckId, userId);
        } catch (DataAccessException ex) {
            throw new RepeatWiseException(RepeatWiseError.INTERNAL_SERVER_ERROR, ex);
        }
    }

    private Map<String, Card> buildCardIndex(List<Card> cards) {
        final Map<String, Card> index = new HashMap<>();
        for (final Card card : cards) {
            index.put(buildKey(card.getFront(), card.getBack()), card);
        }
        return index;
    }

    private String buildKey(String front, String back) {
        return front + "||" + back;
    }

    private ParsedFile parseFile(MultipartFile file) {
        requireFile(file);
        final var filename = file.getOriginalFilename();
        final var extension = StringUtils.substringAfterLast(StringUtils.defaultString(filename), ".").toLowerCase(Locale.ROOT);
        try (InputStream inputStream = file.getInputStream()) {
            if ("csv".equals(extension)) {
                final var records = parseCsv(inputStream);
                return new ParsedFile(records.size(), records);
            }
            if ("xlsx".equals(extension)) {
                final var records = parseXlsx(inputStream);
                return new ParsedFile(records.size(), records);
            }
            throw new RepeatWiseException(RepeatWiseError.IMPORT_INVALID_FORMAT);
        } catch (IOException ex) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_INVALID_FORMAT, ex);
        }
    }

    private List<CsvRecord<ImportRow>> parseCsv(InputStream inputStream) {
        final var schema = CsvSchema.builder()
                .headers(List.of("Front", "Back"))
                .skipHeaderRecord(true)
                .includeHeader(false)
                .build();
        final var helper = CardImportCsvHelper.INSTANCE;
        return this.genericCsvService.read(inputStream, schema, helper);
    }

    private List<CsvRecord<ImportRow>> parseXlsx(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(new BufferedInputStream(inputStream))) {
            final Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                return List.of();
            }
            final var headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) {
                throw new RepeatWiseException(RepeatWiseError.IMPORT_MISSING_COLUMNS);
            }
            final var headerIndexes = resolveHeaderIndexes(headerRow);
            final var frontIndex = headerIndexes.frontIndex();
            final var backIndex = headerIndexes.backIndex();
            if (frontIndex < 0 || backIndex < 0) {
                throw new RepeatWiseException(RepeatWiseError.IMPORT_MISSING_COLUMNS, List.of("Front", "Back"));
            }
            final var formatter = new DataFormatter();
            final List<CsvRecord<ImportRow>> rows = new ArrayList<>();
            for (int i = headerRow.getRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                final Row row = sheet.getRow(i);
                final var front = row != null && frontIndex >= 0 ? formatter.formatCellValue(row.getCell(frontIndex)) : null;
                final var back = row != null && backIndex >= 0 ? formatter.formatCellValue(row.getCell(backIndex)) : null;
                final var data = new ImportRow(front, back);
                final Map<String, String> raw = new HashMap<>();
                raw.put(HEADER_FRONT, front);
                raw.put(HEADER_BACK, back);
                rows.add(new CsvRecord<>(i + 1, data, raw));
            }
            return rows;
        }
    }

    private HeaderIndexes resolveHeaderIndexes(Row row) {
        var frontIndex = -1;
        var backIndex = -1;
        final var formatter = new DataFormatter();
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            final var cellValue = formatter.formatCellValue(row.getCell(i));
            final var normalized = StringUtils.lowerCase(StringUtils.trim(cellValue));
            if (HEADER_FRONT.equals(normalized)) {
                frontIndex = i;
            } else if (HEADER_BACK.equals(normalized)) {
                backIndex = i;
            }
        }
        return new HeaderIndexes(frontIndex, backIndex);
    }

    private void requireFile(MultipartFile file) {
        if ((file == null) || file.isEmpty()) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_FILE_REQUIRED);
        }
    }

    private void validateFileStats(int totalRows, long sizeBytes, Limits limits) {
        final var maxRows = limits.getMaxImportRows();
        if (totalRows == 0) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_EMPTY_FILE);
        }
        if (totalRows > maxRows) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_TOO_MANY_ROWS, totalRows, maxRows);
        }
        final var maxBytes = limits.getMaxFileSizeMb() * 1024L * 1024L;
        if (sizeBytes > maxBytes) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_FILE_TOO_LARGE, limits.getMaxFileSizeMb());
        }
    }

    private record ParsedFile(int totalRows, List<CsvRecord<ImportRow>> rows) {
    }

    private record ImportRow(String front, String back) {
    }

    private static final class CardImportCsvHelper implements CsvHelper<ImportRow> {

        private static final CardImportCsvHelper INSTANCE = new CardImportCsvHelper();
        private static final String[] HEADERS = { "Front", "Back" };

        @Override
        public String[] getHeaders() {
            return HEADERS;
        }

        @Override
        public ImportRow readRecord(Map<String, String> values) {
            final var front = values.get(HEADER_FRONT);
            final var back = values.get(HEADER_BACK);
            return new ImportRow(front, back);
        }

        @Override
        public Map<String, String> writeRecord(ImportRow value) {
            return Map.of(
                    HEADER_FRONT, value.front(),
                    HEADER_BACK, value.back());
        }
    }

    private record RowError(int rowNumber, String messageKey, Object[] args) {
    }

    private record ImportProcessingResult(int imported, int skipped, int failed, List<RowError> errors) {
    }

    private record ImportComputation(int imported, int skipped, int failed, int totalRows, Path errorReportPath) {
    }

    private record HeaderIndexes(int frontIndex, int backIndex) {
    }

    private record RowValidation(boolean invalid, boolean skip, String front, String back, RowError error) {

        static RowValidation invalid(RowError error) {
            return new RowValidation(true, false, null, null, error);
        }

        static RowValidation skip() {
            return new RowValidation(false, true, null, null, null);
        }

        static RowValidation valid(String front, String back) {
            return new RowValidation(false, false, front, back, null);
        }

        boolean isInvalid() {
            return this.invalid;
        }

        boolean shouldSkip() {
            return this.skip;
        }
    }
}

