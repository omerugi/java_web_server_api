package com.example.phonebook_java.controller;

import com.example.phonebook_java.dto.ContactDTO;
import com.example.phonebook_java.exception.phonebook_exception.BadPhonebookRequestException;
import com.example.phonebook_java.exception.phonebook_exception.ContactNotFoundException;
import com.example.phonebook_java.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    private ContactDTO contactDTO;

    @BeforeEach
    void setUp() {
        contactDTO = new ContactDTO();
        contactDTO.setId(1L);
        contactDTO.setFirstName("John");
        contactDTO.setLastName("Doe");
        contactDTO.setPhone("+1234567890");
    }

    @Test
    void getContacts() throws Exception {
        Page<ContactDTO> page = new PageImpl<>(Arrays.asList(contactDTO));
        when(contactService.getContactsDTO(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/contacts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("John"));

        verify(contactService).getContactsDTO(any(PageRequest.class));
    }

    @Test
    void getContacts_InvalidPageSize() throws Exception {
        doThrow(new BadPhonebookRequestException("Invalid page or size"))
                .when(contactService).getContactsDTO(any(PageRequest.class));

        mockMvc.perform(get("/api/contacts")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getContactById() throws Exception {
        when(contactService.getContactDTOById(1L)).thenReturn(contactDTO);

        mockMvc.perform(get("/api/contacts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(contactService).getContactDTOById(1L);
    }

    @Test
    void getContactById_NotFound() throws Exception {
        when(contactService.getContactDTOById(1L)).thenThrow(new ContactNotFoundException("Contact not found"));

        mockMvc.perform(get("/api/contacts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createContact() throws Exception {
        when(contactService.createContact(any(ContactDTO.class))).thenReturn(contactDTO);

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(contactService).createContact(any(ContactDTO.class));
    }

    @Test
    void updateContact() throws Exception {
        when(contactService.updateContact(eq(1L), any(ContactDTO.class))).thenReturn(contactDTO);

        mockMvc.perform(put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(contactService).updateContact(eq(1L), any(ContactDTO.class));
    }

    @Test
    void deleteContact() throws Exception {
        doNothing().when(contactService).deleteContact(1L);

        mockMvc.perform(delete("/api/contacts/1"))
                .andExpect(status().isNoContent());

        verify(contactService).deleteContact(1L);
    }
}
