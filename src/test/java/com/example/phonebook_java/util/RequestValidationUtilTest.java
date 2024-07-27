package com.example.phonebook_java.util;

import com.example.phonebook_java.config.Constant;
import com.example.phonebook_java.exception.phonebook_exception.BadPhonebookRequestException;
import com.example.phonebook_java.model.enums.CountryCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidationUtilTest {

    @Test
    void isValidPageAndSize_ValidInput() {
        assertAll(
                () -> assertTrue(RequestUtil.isValidPageAndSize(0, 10)),
                () -> assertTrue(RequestUtil.isValidPageAndSize(1, 5))
        );
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 5, " + Constant.PAGE_VALUE_ERROR,
            "0, -1, " + Constant.SIZE_VALUE_ERROR,
            "0, 11, " + Constant.SIZE_LIMIT_ERROR
    })
    void isValidPageAndSize_InvalidInput(int page, int size, String expectedMessage) {
        BadPhonebookRequestException exception = assertThrows(BadPhonebookRequestException.class,
                () -> RequestUtil.isValidPageAndSize(page, size));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+12025550139",   // Realistic US phone number with country code
            "202-555-0139",   // U.S. phone number with hyphens
            "(202) 555-0139", // U.S. phone number with parentheses
            "202 555 0139",   // U.S. phone number with spaces
            "2025550139",      // U.S. phone number without any formatting
            "+1 (202) 555-0139" // International format with US country code
    })
    void isValidPhoneNumberUsing_ValidPhoneNumbers(String phoneNumber) {
        assertTrue(RequestUtil.isValidPhoneNumberUsing(phoneNumber, CountryCode.US));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345",
            "1234567890123456",
            "abcdefghij",
            "+123 (45) 678-90-1234567890"
    })
    void isValidPhoneNumberUsing_InvalidPhoneNumbers(String phoneNumber) {
        BadPhonebookRequestException exception = assertThrows(BadPhonebookRequestException.class,
                () -> RequestUtil.isValidPhoneNumberUsing(phoneNumber, CountryCode.US));
        assertEquals(Constant.PHONE_NUMBER_ERROR, exception.getMessage());
    }
}