package com.repeatwise.enums;

public enum RemindStatus {
    PENDING("pending"),      // Chưa gửi notification
    SENT("sent"),           // Đã gửi notification
    DONE("done"),           // User đã ôn xong
    SKIPPED("skipped"),     // User bỏ qua
    RESCHEDULED("rescheduled"), // Lịch nhắc đã thay đổi
    CANCELLED("cancelled"); // Đã hủy

    private final String value;

    RemindStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
} 