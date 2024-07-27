package com.example.phonebook_java.service.impl;

import com.example.phonebook_java.config.Constant;
import com.example.phonebook_java.dto.ContactDTO;
import com.example.phonebook_java.exception.phonebook_exception.ResourceNotFoundException;
import com.example.phonebook_java.mapper.ContactMapper;
import com.example.phonebook_java.model.Contact;
import com.example.phonebook_java.repository.ContactRepository;
import com.example.phonebook_java.service.ContactService;
import com.example.phonebook_java.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@CacheConfig(cacheNames = "contacts")
@Transactional
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    @Override
    @Cacheable(key = "#pageable")
    @Transactional(readOnly = true)
    public Page<ContactDTO> getContactsDTO(Pageable pageable) {
        log.debug("Fetching contacts with pageable: {}", pageable);
        Page<ContactDTO> contacts = contactRepository.findAll(pageable).map(contactMapper::toDTO);
        log.info("Retrieved {} contacts", contacts.getContent().size());
        return contacts;
    }

    @Override
    @Cacheable(key = "#id")
    @Transactional(readOnly = true)
    public ContactDTO getContactDTOById(Long id) {
        log.debug("Fetching contact with id: {}", id);
        Contact contact = getContactById(id);
        return contactMapper.toDTO(contact);
    }

    @Override
    @CachePut(key = "#result.id")
    @Transactional
    public ContactDTO createContact(ContactDTO contactDTO) {
        log.debug("Creating new contact: {}", contactDTO);
        Contact contact = contactMapper.toEntity(contactDTO);
        Contact savedContact = contactRepository.save(contact);
        log.info("Created new contact with id: {}", savedContact.getId());
        return contactMapper.toDTO(savedContact);
    }

    @Override
    @CachePut(key = "#id")
    @Transactional
    public ContactDTO updateContact(Long id, ContactDTO contactDetails) {
        log.debug("Updating contact with id: {}", id);
        Contact contact = getContactById(id);
        Contact toUpdateContact = RequestUtil.updateFiled(contactDetails, contact);
        RequestUtil.isValidPhoneNumberUsing(toUpdateContact.getPhone(), toUpdateContact.getCountryCode());
        Contact updatedContact = contactRepository.save(toUpdateContact);
        log.info("Updated contact with id: {}", updatedContact.getId());
        return contactMapper.toDTO(updatedContact);
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
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

