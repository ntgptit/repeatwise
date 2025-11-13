package com.repeatwise.csv;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 * Tiện ích hỗ trợ Apache Commons CSV.
 */
public final class CsvUtils {

    private CsvUtils() {
        // Utility class
    }

    public static CSVParser newParser(Reader reader, CsvSchema schema) throws IOException {
        final var formatBuilder = CSVFormat.DEFAULT.builder()
                .setIgnoreHeaderCase(schema.isIgnoreHeaderCase())
                .setTrim(schema.isTrim())
                .setSkipHeaderRecord(schema.isSkipHeaderRecord())
                .setDelimiter(schema.getDelimiter());

        if (!schema.getHeaders().isEmpty()) {
            formatBuilder.setHeader(schema.headerArray());
        } else {
            formatBuilder.setHeader();
        }
        return formatBuilder.build().parse(reader);
    }

    public static CSVPrinter newPrinter(Writer writer, CsvSchema schema, String[] headers) throws IOException {
        final var formatBuilder = CSVFormat.DEFAULT.builder()
                .setTrim(schema.isTrim())
                .setIgnoreHeaderCase(schema.isIgnoreHeaderCase())
                .setDelimiter(schema.getDelimiter());
        if (schema.isIncludeHeader()) {
            formatBuilder.setHeader(headers);
        }
        return formatBuilder.build().print(writer);
    }

    public static Map<String, String> toNormalizedMap(CSVRecord record) {
        final Map<String, String> map = new HashMap<>();
        record.toMap().forEach((key, value) -> {
            if (key != null) {
                map.put(normalizeKey(key), value);
            }
        });
        return map;
    }

    public static String normalizeKey(String key) {
        final var normalized = Normalizer.normalize(key, Normalizer.Form.NFKD)
                .toLowerCase(Locale.ROOT)
                .trim();
        return normalized;
    }

    public static String normalizeValue(String value) {
        return value == null ? null : value.trim();
    }

    public static java.io.Reader toUtf8Reader(java.io.InputStream inputStream) throws IOException {
        return new java.io.InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }

    public static java.io.Writer toUtf8Writer(java.io.OutputStream outputStream) throws IOException {
        return new java.io.OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
    }
}
