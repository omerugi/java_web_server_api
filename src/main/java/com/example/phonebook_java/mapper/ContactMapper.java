package com.example.phonebook_java.mapper;

import com.example.phonebook_java.dto.ContactDTO;
import com.example.phonebook_java.model.Contact;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContactMapper {

    public ContactDTO toDTO(Contact contact) {
        log.debug("Converting Contact to DTO");
        ContactDTO dto = new ContactDTO();
        dto.setId(contact.getId());
        dto.setFirstName(contact.getFirstName());
        dto.setLastName(contact.getLastName());
        dto.setPhone(contact.getPhone());
        dto.setCountryCode(contact.getCountryCode());
        dto.setAddress(contact.getAddress());
        return dto;
    }

    public Contact toEntity(ContactDTO dto) {
        log.debug("Converting DTO to Contact");
        Contact contact = new Contact();
        contact.setId(dto.getId());
        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setPhone(dto.getPhone());
        contact.setCountryCode(dto.getCountryCode());
        contact.setAddress(dto.getAddress());
        return contact;
    }
}
