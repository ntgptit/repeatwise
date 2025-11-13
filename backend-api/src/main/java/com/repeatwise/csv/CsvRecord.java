package com.repeatwise.csv;

import java.util.Map;

/**
 * Kết quả đọc CSV kèm dòng gốc.
 */
public record CsvRecord<T>(
        int rowNumber,
        T data,
        Map<String, String> rawValues) {
}
