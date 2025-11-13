package com.repeatwise.enums;

/**
 * Định dạng file export.
 */
public enum ExportFormat {
    CSV("text/csv", "csv"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");

    private final String contentType;
    private final String extension;

    ExportFormat(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getExtension() {
        return this.extension;
    }

    public static ExportFormat fromString(String value) {
        if (value == null) {
            return CSV;
        }
        return ExportFormat.valueOf(value.trim().toUpperCase());
    }
}

