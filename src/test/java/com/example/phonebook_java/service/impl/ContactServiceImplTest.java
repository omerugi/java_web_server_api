package com.example.phonebook_java.service.impl;

import com.example.phonebook_java.dto.ContactDTO;
import com.example.phonebook_java.exception.phonebook_exception.ContactNotFoundException;
import com.example.phonebook_java.mapper.ContactMapper;
import com.example.phonebook_java.model.Contact;
import com.example.phonebook_java.model.enums.CountryCode;
import com.example.phonebook_java.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ContactMapper contactMapper;

    @InjectMocks
    private ContactServiceImpl contactService;

    private Contact contact;
    private ContactDTO contactDTO;

    @BeforeEach
    void setUp() {
        contact = new Contact();
        contact.setId(1L);
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setPhone("+12025550139");
        contact.setCountryCode(CountryCode.US);

        contactDTO = new ContactDTO();
        contactDTO.setId(1L);
        contactDTO.setFirstName("John");
        contactDTO.setLastName("Doe");
        contactDTO.setPhone("+12025550139");
    }

    @Test
    void getContactsDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> contactPage = new PageImpl<>(Arrays.asList(contact));

        when(contactRepository.findAll(pageable)).thenReturn(contactPage);
        when(contactMapper.toDTO(any(Contact.class))).thenReturn(contactDTO);

        Page<ContactDTO> result = contactService.getContactsDTO(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(contactDTO, result.getContent().get(0));
        verify(contactRepository).findAll(pageable);
        verify(contactMapper).toDTO(any(Contact.class));
    }

    @Test
    void getContactDTOById() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(contactMapper.toDTO(contact)).thenReturn(contactDTO);

        ContactDTO result = contactService.getContactDTOById(1L);

        assertNotNull(result);
        assertEquals(contactDTO, result);
        verify(contactRepository).findById(1L);
        verify(contactMapper).toDTO(contact);
    }

    @Test
    void getContactDTOById_NotFound() {
        when(contactRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class, () -> contactService.getContactDTOById(1L));
        verify(contactRepository).findById(1L);
    }

    @Test
    void createContact() {
        when(contactMapper.toEntity(contactDTO)).thenReturn(contact);
        when(contactRepository.save(contact)).thenReturn(contact);
        when(contactMapper.toDTO(contact)).thenReturn(contactDTO);

        ContactDTO result = contactService.createContact(contactDTO);

        assertNotNull(result);
        assertEquals(contactDTO, result);
        verify(contactMapper).toEntity(contactDTO);
        verify(contactRepository).save(contact);
        verify(contactMapper).toDTO(contact);
    }

    @Test
    void updateContact() {
        ContactDTO updatedDTO = new ContactDTO();
        updatedDTO.setFirstName("Jane");
        updatedDTO.setLastName("Smith");
        updatedDTO.setPhone("+12025550139");

        Contact updatedContact = new Contact();
        updatedContact.setId(1L);
        updatedContact.setFirstName("Jane");
        updatedContact.setLastName("Smith");
        updatedContact.setPhone("+12025550139");
        updatedContact.setCountryCode(CountryCode.US);

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setId(1L);
        contactDTO.setFirstName("Jane");
        contactDTO.setLastName("Smith");
        contactDTO.setPhone("+12025550139");
        contactDTO.setCountryCode(CountryCode.US);

        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);
        when(contactMapper.toDTO(any(Contact.class))).thenReturn(contactDTO);

        ContactDTO result = contactService.updateContact(1L, updatedDTO);

        assertNotNull(result);
        assertEquals(updatedDTO.getFirstName(), result.getFirstName());
        assertEquals(updatedDTO.getLastName(), result.getLastName());
        assertEquals(updatedDTO.getPhone(), result.getPhone());
        verify(contactRepository).findById(1L);
        verify(contactRepository).save(any(Contact.class));
        verify(contactMapper).toDTO(updatedContact);
    }

    @Test
    void deleteContact() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));

        contactService.deleteContact(1L);

        verify(contactRepository).findById(1L);
        verify(contactRepository).delete(contact);
    }

    @Test
    void deleteContact_NotFound() {
        when(contactRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class, () -> contactService.deleteContact(1L));
        verify(contactRepository).findById(1L);
        verify(contactRepository, never()).delete(any(Contact.class));
    }

    @Test
    void testSearchContacts() {
        String searchTerm = "John";
        Pageable pageable = PageRequest.of(0, 10);

        Contact contact1 = new Contact();
        contact1.setId(1L);
        contact1.setFirstName("John");
        contact1.setLastName("Doe");
        contact1.setPhone("1234567890");
        contact1.setAddress("123 Main St");

        Contact contact2 = new Contact();
        contact2.setId(2L);
        contact2.setFirstName("Jane");
        contact2.setLastName("Doe");
        contact2.setPhone("0987654321");
        contact2.setAddress("456 Elm St");

        List<Contact> contacts = Arrays.asList(contact1, contact2);
        Page<Contact> contactPage = new PageImpl<>(contacts, pageable, contacts.size());

        ContactDTO contactDTO1 = new ContactDTO();
        contactDTO1.setId(1L);
        contactDTO1.setFirstName("John");
        contactDTO1.setLastName("Doe");
        contactDTO1.setPhone("1234567890");
        contactDTO1.setAddress("123 Main St");

        ContactDTO contactDTO2 = new ContactDTO();
        contactDTO2.setId(2L);
        contactDTO2.setFirstName("Jane");
        contactDTO2.setLastName("Doe");
        contactDTO2.setPhone("0987654321");
        contactDTO2.setAddress("456 Elm St");

        when(contactRepository.searchContacts(searchTerm, pageable)).thenReturn(contactPage);
        when(contactMapper.toDTO(contact1)).thenReturn(contactDTO1);
        when(contactMapper.toDTO(contact2)).thenReturn(contactDTO2);

        Page<ContactDTO> result = contactService.searchContacts(searchTerm, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getFirstName());
        assertEquals("Jane", result.getContent().get(1).getFirstName());
        verify(contactRepository, times(1)).searchContacts(searchTerm, pageable);
        verify(contactMapper, times(2)).toDTO(any(Contact.class));
    }
}
