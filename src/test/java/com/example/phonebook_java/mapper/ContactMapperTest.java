package com.example.phonebook_java.mapper;

import com.example.phonebook_java.dto.ContactDTO;
import com.example.phonebook_java.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContactMapperTest {

    private ContactMapper contactMapper;

    @BeforeEach
    void setUp() {
        contactMapper = new ContactMapper();
    }

    @Test
    void testToDTO() {
        // Given
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setPhone("+1234567890");

        // When
        ContactDTO dto = contactMapper.toDTO(contact);

        // Then
        assertNotNull(dto);
        assertEquals(contact.getId(), dto.getId());
        assertEquals(contact.getFirstName(), dto.getFirstName());
        assertEquals(contact.getLastName(), dto.getLastName());
        assertEquals(contact.getPhone(), dto.getPhone());
    }

    @Test
    void testToEntity() {
        // Given
        ContactDTO dto = new ContactDTO();
        dto.setId(1L);
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setPhone("+0987654321");

        // When
        Contact contact = contactMapper.toEntity(dto);

        // Then
        assertNotNull(contact);
        assertEquals(dto.getId(), contact.getId());
        assertEquals(dto.getFirstName(), contact.getFirstName());
        assertEquals(dto.getLastName(), contact.getLastName());
        assertEquals(dto.getPhone(), contact.getPhone());
    }
}
