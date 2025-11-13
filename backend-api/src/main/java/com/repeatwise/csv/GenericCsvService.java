package com.repeatwise.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;

import lombok.extern.slf4j.Slf4j;

/**
 * Service generic để đọc/ghi CSV dựa trên CsvHelper.
 */
@Component
@Slf4j
public class GenericCsvService {

    public <T> List<CsvRecord<T>> read(InputStream inputStream, CsvSchema schema, CsvHelper<T> helper) {
        try (Reader reader = CsvUtils.toUtf8Reader(inputStream);
                CSVParser parser = CsvUtils.newParser(reader, schema)) {
            validateHeaders(parser, schema, helper.getHeaders());

            final List<CsvRecord<T>> results = new ArrayList<>();
            for (CSVRecord record : parser) {
                final Map<String, String> values = CsvUtils.toNormalizedMap(record);
                final T data = helper.readRecord(values);
                results.add(new CsvRecord<>((int) record.getRecordNumber(), data, values));
            }
            return results;
        } catch (IOException ex) {
            log.error("Không thể đọc CSV", ex);
            throw new RepeatWiseException(RepeatWiseError.IMPORT_INVALID_FORMAT, ex.getMessage());
        }
    }

    public <T> void write(OutputStream outputStream, CsvSchema schema, CsvHelper<T> helper, List<T> items) {
        final String[] headers = helper.getHeaders();
        try (Writer writer = CsvUtils.toUtf8Writer(outputStream);
                CSVPrinter printer = CsvUtils.newPrinter(writer, schema, headers)) {
            if (schema.isIncludeHeader()) {
                printer.printRecord((Object[]) headers);
            }
            for (T item : items) {
                final Map<String, String> row = helper.writeRecord(item);
                final List<String> ordered = new ArrayList<>(headers.length);
                for (final String header : headers) {
                    final String key = CsvUtils.normalizeKey(header);
                    ordered.add(row.getOrDefault(key, ""));
                }
                printer.printRecord(ordered);
            }
        } catch (IOException ex) {
            log.error("Không thể ghi CSV", ex);
            throw new RepeatWiseException(RepeatWiseError.EXPORT_GENERATION_FAILED, ex.getMessage());
        }
    }

    private void validateHeaders(CSVParser parser, CsvSchema schema, String[] expectedHeaders) {
        if (expectedHeaders == null || expectedHeaders.length == 0) {
            return;
        }
        final var headerMap = parser.getHeaderMap();
        if (headerMap == null || headerMap.isEmpty()) {
            throw new RepeatWiseException(RepeatWiseError.IMPORT_MISSING_COLUMNS, Arrays.toString(expectedHeaders));
        }
        final var normalizedHeaderSet = headerMap.keySet().stream()
                .map(CsvUtils::normalizeKey)
                .toList();
        for (final String header : expectedHeaders) {
            final var normalized = CsvUtils.normalizeKey(header);
            final boolean exists = normalizedHeaderSet.stream()
                    .anyMatch(h -> h.equals(normalized));
            if (!exists) {
                throw new RepeatWiseException(RepeatWiseError.IMPORT_MISSING_COLUMNS, header);
            }
        }
    }
}
