package com.example.phonebook_java.repository;

import com.example.phonebook_java.model.Contact;
import com.example.phonebook_java.model.enums.CountryCode;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ContactRepositoryTest {


    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateContact() {
        Contact contact = new Contact();
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setPhone("+1234567890");
        contact.setCountryCode(CountryCode.US);

        Contact savedContact = contactRepository.save(contact);
        entityManager.flush();
        entityManager.clear();

        assertNotNull(savedContact.getId());
        assertEquals("John", savedContact.getFirstName());
        assertEquals("Doe", savedContact.getLastName());
        assertEquals("+1234567890", savedContact.getPhone());
        assertNotNull(savedContact.getCreatedAt());
        assertNotNull(savedContact.getUpdatedAt());
    }

    @Test
    void testUpdateContact() {
        // Given
        Contact contact = new Contact();
        contact.setFirstName("Jane");
        contact.setLastName("Doe");
        contact.setPhone("+0987654321");
        contact.setCountryCode(CountryCode.US);

        Contact savedContact = contactRepository.save(contact);
        entityManager.flush();
        entityManager.clear();

        // When
        Contact retrievedContact = contactRepository.findById(savedContact.getId()).orElseThrow();
        LocalDateTime originalUpdatedAt = retrievedContact.getUpdatedAt();

        retrievedContact.setFirstName("Janet");
        Contact updatedContact = contactRepository.save(retrievedContact);
        entityManager.flush();
        entityManager.clear();

        // Then
        Contact finalContact = contactRepository.findById(updatedContact.getId()).orElseThrow();
        assertEquals("Janet", finalContact.getFirstName());
        assertNotNull(finalContact.getUpdatedAt());
        assertTrue(finalContact.getUpdatedAt().isAfter(originalUpdatedAt),
                "Updated timestamp should be after the original timestamp");
    }

    @Test
    void testDeleteContact() {
        Contact contact = new Contact();
        contact.setFirstName("Alice");
        contact.setLastName("Smith");
        contact.setPhone("+1122334455");
        contact.setCountryCode(CountryCode.US);

        Contact savedContact = contactRepository.save(contact);
        contactRepository.deleteById(savedContact.getId());
        entityManager.flush();
        entityManager.clear();

        Optional<Contact> deletedContact = contactRepository.findById(savedContact.getId());
        assertFalse(deletedContact.isPresent());
    }

    @Test
    void testFirstNameNotNull() {
        Contact contact = new Contact();
        contact.setLastName("Doe");
        contact.setPhone("+1234567890");
        contact.setCountryCode(CountryCode.US);

        assertThrows(DataIntegrityViolationException.class, () -> {
            contactRepository.save(contact);
            entityManager.flush();
        });
    }

    @Test
    void testLastNameNotNull() {
        Contact contact = new Contact();
        contact.setFirstName("John");
        contact.setPhone("+1234567890");
        contact.setCountryCode(CountryCode.US);

        assertThrows(DataIntegrityViolationException.class, () -> {
            contactRepository.save(contact);
            entityManager.flush();
        });
    }

    @Test
    void testPhoneNotNull() {
        Contact contact = new Contact();
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setCountryCode(CountryCode.US);

        assertThrows(DataIntegrityViolationException.class, () -> {
            contactRepository.save(contact);
            entityManager.flush();
        });
    }

    @Test
    void testCreatedAtAutomaticallySet() {
        Contact contact = new Contact();
        contact.setFirstName("Bob");
        contact.setLastName("Brown");
        contact.setPhone("+9876543210");
        contact.setCountryCode(CountryCode.US);

        Contact savedContact = contactRepository.save(contact);
        entityManager.flush();
        entityManager.clear();
        assertNotNull(savedContact.getCreatedAt());
    }

    @Test
    void testUpdatedAtAutomaticallyUpdated() {
        // Given
        Contact contact = new Contact();
        contact.setFirstName("Charlie");
        contact.setLastName("Chaplin");
        contact.setPhone("+1357924680");
        contact.setCountryCode(CountryCode.US);

        Contact savedContact = contactRepository.save(contact);
        entityManager.flush();
        entityManager.clear();

        // When
        Contact retrievedContact = contactRepository.findById(savedContact.getId()).orElseThrow();
        LocalDateTime firstUpdateTime = retrievedContact.getUpdatedAt();

        retrievedContact.setPhone("+0246813579");
        Contact updatedContact = contactRepository.save(retrievedContact);
        entityManager.flush();
        entityManager.clear();

        // Then
        Contact finalContact = contactRepository.findById(updatedContact.getId()).orElseThrow();
        assertNotNull(finalContact.getUpdatedAt());
        assertTrue(finalContact.getUpdatedAt().isAfter(firstUpdateTime),
                "Updated timestamp should be after the original timestamp");
    }

    @Test
    void testSearchContacts() {
        // Given
        Contact contact1 = new Contact();
        contact1.setFirstName("John");
        contact1.setLastName("Doe");
        contact1.setPhone("1234567890");
        contact1.setCountryCode(CountryCode.US);
        contact1.setAddress("123 Main St");
        contactRepository.save(contact1);
        entityManager.flush();
        entityManager.clear();

        Contact contact2 = new Contact();
        contact2.setFirstName("Jane");
        contact2.setLastName("Doe");
        contact2.setPhone("0987654321");
        contact2.setAddress("456 Elm St");
        contactRepository.save(contact2);
        entityManager.flush();
        entityManager.clear();

        Contact contact3 = new Contact();
        contact3.setFirstName("Alice");
        contact3.setLastName("Smith");
        contact3.setPhone("1122334455");
        contact3.setAddress("789 Oak St");
        contactRepository.save(contact3);
        entityManager.flush();
        entityManager.clear();

        // When & Then
        // Search by first name
        Page<Contact> result = contactRepository.searchContacts("John", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");

        // Search by last name
        result = contactRepository.searchContacts("Doe", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Contact::getLastName).containsOnly("Doe");

        // Search by phone
        result = contactRepository.searchContacts("1122", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPhone()).isEqualTo("1122334455");

        // Search by address
        result = contactRepository.searchContacts("Elm", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAddress()).isEqualTo("456 Elm St");

        // Search with no results
        result = contactRepository.searchContacts("Nonexistent", PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();

        // Test pagination
        result = contactRepository.searchContacts("Doe", PageRequest.of(0, 1));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void testUniquePhoneNumber() {
        // Given
        Contact contact1 = new Contact();
        contact1.setFirstName("John");
        contact1.setLastName("Doe");
        contact1.setPhone("1234567890");
        contact1.setAddress("123 Main St");
        contactRepository.save(contact1);
        entityManager.flush();
        entityManager.clear();

        // When
        Contact contact2 = new Contact();
        contact2.setFirstName("Jane");
        contact2.setLastName("Doe");
        contact2.setPhone("1234567890");  // Same phone number as contact1
        contact2.setAddress("456 Elm St");

        // Then
        assertThatThrownBy(() -> {
            contactRepository.save(contact2);
            entityManager.flush();
            entityManager.clear();})
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void testCaseInsensitiveSearch() {
        // Given
        Contact contact = new Contact();
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setPhone("1234567890");
        contact.setAddress("123 Main St");
        contactRepository.save(contact);
        entityManager.flush();
        entityManager.clear();

        // When & Then
        Page<Contact> result = contactRepository.searchContacts("john", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");

        result = contactRepository.searchContacts("MAIN", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAddress()).isEqualTo("123 Main St");
    }

}
