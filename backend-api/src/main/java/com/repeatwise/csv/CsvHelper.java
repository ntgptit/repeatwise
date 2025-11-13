package com.repeatwise.csv;

import java.util.Map;

/**
 * Contract cho mapping CSV record <-> object.
 */
public interface CsvHelper<T> {

    /**
     * Trả về danh sách header theo thứ tự mong muốn.
     */
    String[] getHeaders();

    /**
     * Chuyển đổi một dòng CSV (map key->value đã chuẩn hóa) thành object.
     */
    T readRecord(Map<String, String> values);

    /**
     * Chuyển object thành map key->value tương ứng với header.
     */
    Map<String, String> writeRecord(T value);
}
