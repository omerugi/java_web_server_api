package com.example.phonebook_java.controller;

import com.example.phonebook_java.config.Constant;
import com.example.phonebook_java.dto.ContactDTO;
import com.example.phonebook_java.model.Contact;
import com.example.phonebook_java.model.enums.CountryCode;
import com.example.phonebook_java.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ContactControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ContactDTO validContact;

    @Test
    void testSearchContacts() throws Exception {
        // Given
        Contact contact1 = new Contact();
        contact1.setFirstName("John");
        contact1.setLastName("Doe");
        contact1.setPhone("+972586589400");
        contact1.setAddress("123 Main St");
        contact1.setCountryCode(CountryCode.IL);
        contactRepository.save(contact1);

        Contact contact2 = new Contact();
        contact2.setFirstName("Jane");
        contact2.setLastName("Doe");
        contact2.setPhone("+972586589300");
        contact2.setAddress("456 Elm St");
        contact2.setCountryCode(CountryCode.IL);
        contactRepository.save(contact2);

        // When & Then
        mockMvc.perform(get("/api/contacts/search")
                        .param("searchTerm", "Doe")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[0].countryCode").value("IL"))
                .andExpect(jsonPath("$.content[1].firstName").value("Jane"))
                .andExpect(jsonPath("$.content[1].countryCode").value("IL"));

        mockMvc.perform(get("/api/contacts/search")
                        .param("searchTerm", "Main")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[0].countryCode").value("IL"));
    }


    @BeforeEach
    void setUp() {
        validContact = new ContactDTO();
        validContact.setFirstName("John");
        validContact.setLastName("Doe");
        validContact.setPhone("+972504065233");
        validContact.setCountryCode(CountryCode.IL);
        validContact.setAddress("123 Test St");
    }

    @Test
    void testGetContacts() throws Exception {
        mockMvc.perform(get("/api/contacts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", isA(java.util.List.class)))
                .andExpect(jsonPath("$.totalElements", isA(Number.class)));
    }

    @Test
    void testGetContactById() throws Exception {
        ContactDTO savedContact = createContact(validContact);

        mockMvc.perform(get("/api/contacts/{id}", savedContact.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedContact.getId().intValue())))
                .andExpect(jsonPath("$.firstName", is(savedContact.getFirstName())));
    }

    @Test
    void testGetContactById_NotFound() throws Exception {
        mockMvc.perform(get("/api/contacts/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Contact not found")));
    }

    @Test
    void testCreateContact() throws Exception {
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validContact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.firstName", is(validContact.getFirstName())));
    }

    @Test
    void testCreateContact_ValidationFirstNameError() throws Exception {
        validContact.setFirstName("");
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validContact)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(Constant.FIRST_NAME_REQUIRED)));
    }

    @Test
    void testCreateContact_ValidationLastNameError() throws Exception {
        validContact.setLastName("");
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validContact)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(Constant.LAST_NAME_REQUIRED)));
    }

    @Test
    void testCreateContact_ValidationPhoneEmptyError() throws Exception {
        validContact.setPhone("");
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validContact)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(Constant.PHONE_REQUIRED)));
    }

    @Test
    void testCreateContact_ValidationPhoneError() throws Exception {
        validContact.setPhone("565");
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validContact)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(Constant.PHONE_NUMBER_ERROR)));
    }

    @Test
    void testCreateContact_ValidationPhoneNumberError() throws Exception {
        validContact.setPhone("+9150505011");
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validContact)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(Constant.PHONE_NUMBER_ERROR)));
    }

    @Test
    void testUpdateContact() throws Exception {
        ContactDTO savedContact = createContact(validContact);
        savedContact.setFirstName("Jane");

        mockMvc.perform(put("/api/contacts/{id}", savedContact.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedContact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedContact.getId().intValue())))
                .andExpect(jsonPath("$.firstName", is("Jane")));
    }

    @Test
    void testUpdateContactEmptyAddress() throws Exception {
        ContactDTO savedContact = createContact(validContact);
        savedContact.setAddress("");

        mockMvc.perform(put("/api/contacts/{id}", savedContact.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedContact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is("")));
    }

    @Test
    void testUpdateContactNullAddress() throws Exception {
        ContactDTO savedContact = createContact(validContact);
        savedContact.setAddress(null);

        mockMvc.perform(put("/api/contacts/{id}", savedContact.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedContact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is(validContact.getAddress())));
    }

    @Test
    void testUpdateContact_ValidationNotActive() throws Exception {
        ContactDTO savedContact = createContact(validContact);
        savedContact.setFirstName("");

        mockMvc.perform(put("/api/contacts/{id}", savedContact.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedContact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    void testDeleteContact() throws Exception {
        ContactDTO savedContact = createContact(validContact);

        mockMvc.perform(delete("/api/contacts/{id}", savedContact.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/contacts/{id}", savedContact.getId()))
                .andExpect(status().isNotFound());
    }


    @Test
    void testInvalidPageSizeLimit() throws Exception {
        mockMvc.perform(get("/api/contacts")
                        .param("page", "0")
                        .param("size", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(Constant.SIZE_LIMIT_ERROR)));
    }


    @Test
    void testNegativePage() throws Exception {
        mockMvc.perform(get("/api/contacts")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(Constant.PAGE_VALUE_ERROR)));
    }

    @Test
    void testNegativeSize() throws Exception {
        mockMvc.perform(get("/api/contacts")
                        .param("page", "0")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(Constant.SIZE_VALUE_ERROR)));
    }

    @Test
    void testDefaultValues() throws Exception {
        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    private ContactDTO createContact(ContactDTO contactDTO) throws Exception {
        String response = mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, ContactDTO.class);
    }
}
