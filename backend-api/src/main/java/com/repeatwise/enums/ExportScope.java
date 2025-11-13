package com.repeatwise.enums;

/**
 * Phạm vi export thẻ.
 */
public enum ExportScope {
    ALL,
    DUE_ONLY;

    public static ExportScope fromString(String value) {
        if (value == null) {
            return ALL;
        }
        return ExportScope.valueOf(value.trim().toUpperCase());
    }
}

