package com.example.phonebook_java.service;

import com.example.phonebook_java.dto.ContactDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactService {
    Page<ContactDTO> getContactsDTO(Pageable pageable);
    ContactDTO getContactDTOById(Long id);
    ContactDTO createContact(ContactDTO contactDTO);
    ContactDTO updateContact(Long id, ContactDTO contactDTO);
    void deleteContact(Long id);
    Page<ContactDTO> searchContacts(String searchTerm, Pageable pageable);
}

