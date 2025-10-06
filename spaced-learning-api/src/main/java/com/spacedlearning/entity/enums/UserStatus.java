package com.spacedlearning.entity.enums;

import lombok.Getter;

/**
 * Enum representing possible user statuses.
 */
@Getter
public enum UserStatus {
    ACTIVE("active"), 
    INACTIVE("inactive"), 
    SUSPENDED("suspended");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}