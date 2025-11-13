package com.repeatwise.enums;

/**
 * Chính sách xử lý trùng thẻ khi import.
 */
public enum DuplicateHandlingPolicy {
    SKIP,
    REPLACE,
    KEEP_BOTH;

    /**
     * Parse từ chuỗi (không phân biệt hoa thường).
     *
     * @param value giá trị chuỗi
     * @return enum tương ứng
     * @throws IllegalArgumentException nếu không hợp lệ
     */
    public static DuplicateHandlingPolicy fromString(String value) {
        if (value == null) {
            return SKIP;
        }
        return DuplicateHandlingPolicy.valueOf(value.trim().toUpperCase());
    }
}

