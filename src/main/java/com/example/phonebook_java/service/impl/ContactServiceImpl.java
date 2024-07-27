package com.example.phonebook_java.service.impl;

import com.example.phonebook_java.config.Constant;
import com.example.phonebook_java.dto.ContactDTO;
import com.example.phonebook_java.dto.ContactUpdateDTO;
import com.example.phonebook_java.exception.phonebook_exception.ResourceNotFoundException;
import com.example.phonebook_java.mapper.ContactMapper;
import com.example.phonebook_java.model.Contact;
import com.example.phonebook_java.repository.ContactRepository;
import com.example.phonebook_java.service.ContactService;
import com.example.phonebook_java.util.RequestValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    @Override
    public Page<ContactDTO> getContactsDTO(Pageable pageable) {
        log.debug("Fetching contacts with pageable: {}", pageable);
        Page<ContactDTO> contacts = contactRepository.findAll(pageable).map(contactMapper::toDTO);
        log.info("Retrieved {} contacts", contacts.getContent().size());
        return contacts;
    }

    @Override
    public ContactDTO getContactDTOById(Long id) {
        log.debug("Fetching contact with id: {}", id);
        Contact contact = getContactById(id);
        return contactMapper.toDTO(contact);
    }

    @Override
    public ContactDTO createContact(ContactDTO contactDTO) {
        log.debug("Creating new contact: {}", contactDTO);
        Contact contact = contactMapper.toEntity(contactDTO);
        Contact savedContact = contactRepository.save(contact);
        log.info("Created new contact with id: {}", savedContact.getId());
        return contactMapper.toDTO(savedContact);
    }

    @Override
    public ContactDTO updateContact(Long id, ContactUpdateDTO contactDetails) {
        log.debug("Updating contact with id: {}", id);
        Contact contact = getContactById(id);
        Contact toUpdateContact = updateFiled(contactDetails, contact);
        RequestValidationUtil.isValidPhoneNumberUsing(toUpdateContact.getPhone(), toUpdateContact.getCountryCode());
        Contact updatedContact = contactRepository.save(toUpdateContact);
        log.info("Updated contact with id: {}", updatedContact.getId());
        return contactMapper.toDTO(updatedContact);
    }

    private Contact updateFiled(ContactUpdateDTO contactDetails, Contact contact) {
        if(StringUtils.isNotEmpty(contactDetails.getFirstName()) && !contactDetails.getFirstName().equals(contact.getFirstName()))
            contact.setFirstName(contactDetails.getFirstName());
        if(StringUtils.isNotEmpty(contactDetails.getLastName()) && !contactDetails.getLastName().equals(contact.getLastName()))
            contact.setLastName(contactDetails.getLastName());
        if(contactDetails.getCountryCode() != null && !contactDetails.getCountryCode().equals(contact.getCountryCode()))
            contact.setCountryCode(contactDetails.getCountryCode());
        if(StringUtils.isNotEmpty(contactDetails.getPhone()) && !contactDetails.getPhone().equals(contact.getPhone()))
            contact.setPhone(contactDetails.getPhone());
        return contact;
    }

    @Override
    public void deleteContact(Long id) {
        log.debug("Deleting contact with id: {}", id);
        Contact contact = getContactById(id);
        contactRepository.delete(contact);
        log.info("Deleted contact with id: {}", id);
    }

    private Contact getContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Contact not found with id: {}", id);
                    return new ResourceNotFoundException(Constant.CONTACT_NOT_FOUND_ERROR + id);
                });
    }

}

