package com.repeatwise.csv;

import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * Cấu hình đọc/ghi CSV.
 */
@Getter
@Builder
public class CsvSchema {

    @Builder.Default
    private final List<String> headers = List.of();

    @Builder.Default
    private final char delimiter = ',';

    @Builder.Default
    private final boolean skipHeaderRecord = true;

    @Builder.Default
    private final boolean includeHeader = true;

    @Builder.Default
    private final boolean ignoreHeaderCase = true;

    @Builder.Default
    private final boolean trim = true;

    public String[] headerArray() {
        return this.headers.toArray(String[]::new);
    }

    public static CsvSchema fromHeaders(String... headers) {
        return CsvSchema.builder()
                .headers(Arrays.stream(headers).toList())
                .build();
    }
}
