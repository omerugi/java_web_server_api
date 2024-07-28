package com.example.phonebook_java.util;

import com.example.phonebook_java.config.Constant;
import com.example.phonebook_java.dto.ContactDTO;
import com.example.phonebook_java.exception.phonebook_exception.BadPhonebookRequestException;
import com.example.phonebook_java.model.Contact;
import com.example.phonebook_java.model.enums.CountryCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidationUtilTest {

    @ParameterizedTest
    @CsvSource({
            "+12025550139, US",
            "202-555-0139, US",
            "(202) 555-0139, US",
            "202 555 0139, US",
            "2025550139, US",
            "+1 (202) 555-0139, US",
            "+447911123456, GB",
            "07911 123456, GB",
            "+33123456789, FR",
            "01 23 45 67 89, FR"
    })
    void isValidPhoneNumberUsing_ValidPhoneNumbers(String phoneNumber, CountryCode countryCode) {
        assertTrue(RequestUtil.isValidPhoneNumberUsing(phoneNumber, countryCode));
    }

    @ParameterizedTest
    @CsvSource({
            "12345, US",
            "1234567890123456, US",
            "abcdefghij, US",
            "+123 (45) 678-90-1234567890, US",
            "123, GB",
            "abcdef, FR"
    })
    void isValidPhoneNumberUsing_InvalidPhoneNumbers(String phoneNumber, CountryCode countryCode) {
        BadPhonebookRequestException exception = assertThrows(BadPhonebookRequestException.class,
                () -> RequestUtil.isValidPhoneNumberUsing(phoneNumber, countryCode));
        assertEquals(Constant.PHONE_NUMBER_ERROR, exception.getMessage());
    }

    @Test
    void updateFiled_AllFieldsUpdated() {
        Contact contactFromDB = new Contact();
        contactFromDB.setId(1L);
        contactFromDB.setFirstName("John");
        contactFromDB.setLastName("Doe");
        contactFromDB.setPhone("+12025550139");
        contactFromDB.setCountryCode(CountryCode.US);
        contactFromDB.setAddress("123 Old St");

        ContactDTO contactFromReq = new ContactDTO();
        contactFromReq.setFirstName("Jane");
        contactFromReq.setLastName("Smith");
        contactFromReq.setPhone("+447911123456");
        contactFromReq.setCountryCode(CountryCode.GB);
        contactFromReq.setAddress("456 New St");

        Contact updatedContact = RequestUtil.updateFiled(contactFromReq, contactFromDB);

        assertEquals(1L, updatedContact.getId());
        assertEquals("Jane", updatedContact.getFirstName());
        assertEquals("Smith", updatedContact.getLastName());
        assertEquals("+447911123456", updatedContact.getPhone());
        assertEquals(CountryCode.GB, updatedContact.getCountryCode());
        assertEquals("456 New St", updatedContact.getAddress());
    }

    @Test
    void updateFiled_PartialUpdate() {
        Contact contactFromDB = new Contact();
        contactFromDB.setId(1L);
        contactFromDB.setFirstName("John");
        contactFromDB.setLastName("Doe");
        contactFromDB.setPhone("+12025550139");
        contactFromDB.setCountryCode(CountryCode.US);
        contactFromDB.setAddress("123 Old St");

        ContactDTO contactFromReq = new ContactDTO();
        contactFromReq.setFirstName("Jane");
        contactFromReq.setPhone("+447911123456");

        Contact updatedContact = RequestUtil.updateFiled(contactFromReq, contactFromDB);

        assertEquals(1L, updatedContact.getId());
        assertEquals("Jane", updatedContact.getFirstName());
        assertEquals("Doe", updatedContact.getLastName());
        assertEquals("+447911123456", updatedContact.getPhone());
        assertEquals(CountryCode.US, updatedContact.getCountryCode());
        assertEquals("123 Old St", updatedContact.getAddress());
    }

    @Test
    void updateFiled_NoChanges() {
        Contact contactFromDB = new Contact();
        contactFromDB.setId(1L);
        contactFromDB.setFirstName("John");
        contactFromDB.setLastName("Doe");
        contactFromDB.setPhone("+12025550139");
        contactFromDB.setCountryCode(CountryCode.US);
        contactFromDB.setAddress("123 Old St");

        ContactDTO contactFromReq = new ContactDTO();

        Contact updatedContact = RequestUtil.updateFiled(contactFromReq, contactFromDB);

        assertEquals(1L, updatedContact.getId());
        assertEquals("John", updatedContact.getFirstName());
        assertEquals("Doe", updatedContact.getLastName());
        assertEquals("+12025550139", updatedContact.getPhone());
        assertEquals(CountryCode.US, updatedContact.getCountryCode());
        assertEquals("123 Old St", updatedContact.getAddress());
    }

    @Test
    void updateFiled_EmptyAddress() {
        Contact contactFromDB = new Contact();
        contactFromDB.setId(1L);
        contactFromDB.setFirstName("John");
        contactFromDB.setLastName("Doe");
        contactFromDB.setPhone("+12025550139");
        contactFromDB.setCountryCode(CountryCode.US);
        contactFromDB.setAddress("123 Old St");

        ContactDTO contactFromReq = new ContactDTO();
        contactFromReq.setAddress("");

        Contact updatedContact = RequestUtil.updateFiled(contactFromReq, contactFromDB);

        assertEquals(1L, updatedContact.getId());
        assertEquals("John", updatedContact.getFirstName());
        assertEquals("Doe", updatedContact.getLastName());
        assertEquals("+12025550139", updatedContact.getPhone());
        assertEquals(CountryCode.US, updatedContact.getCountryCode());
        assertEquals("", updatedContact.getAddress());
    }

    @Test
    void updateFiled_NullAddress() {
        Contact contactFromDB = new Contact();
        contactFromDB.setId(1L);
        contactFromDB.setFirstName("John");
        contactFromDB.setLastName("Doe");
        contactFromDB.setPhone("+12025550139");
        contactFromDB.setCountryCode(CountryCode.US);
        contactFromDB.setAddress("123 Old St");

        ContactDTO contactFromReq = new ContactDTO();
        contactFromReq.setAddress(null);

        Contact updatedContact = RequestUtil.updateFiled(contactFromReq, contactFromDB);

        assertEquals(1L, updatedContact.getId());
        assertEquals("John", updatedContact.getFirstName());
        assertEquals("Doe", updatedContact.getLastName());
        assertEquals("+12025550139", updatedContact.getPhone());
        assertEquals(CountryCode.US, updatedContact.getCountryCode());
        assertEquals("123 Old St", updatedContact.getAddress());
    }
}