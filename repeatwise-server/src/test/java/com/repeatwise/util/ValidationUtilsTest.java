package com.repeatwise.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void isValidUuid_ShouldAcceptUppercaseLetters() {
        String uppercaseUuid = "A0EEBBAD-5D1A-11D1-9A0C-0000F8752B83";
        assertTrue(ValidationUtils.isValidUuid(uppercaseUuid));
    }

    @Test
    void isValidUuid_ShouldRejectInvalidFormat() {
        String invalidUuid = "invalid-uuid";
        assertFalse(ValidationUtils.isValidUuid(invalidUuid));
    }
}
