package com.example.phonebook_java.repository;

import com.example.phonebook_java.model.Contact;
import com.example.phonebook_java.model.enums.CountryCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

}
