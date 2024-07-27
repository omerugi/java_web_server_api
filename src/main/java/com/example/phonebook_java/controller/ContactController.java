package com.example.phonebook_java.controller;

import com.example.phonebook_java.dto.ContactDTO;
import com.example.phonebook_java.model.Contact;
import com.example.phonebook_java.service.ContactService;
import com.example.phonebook_java.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
@Slf4j
@Tag(name = "Contact", description = "Contact management APIs")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    @Operation(summary = "Get all contacts", description = "Get a paginated list of all contacts")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = Page.class)))
    public ResponseEntity<Page<ContactDTO>> getContacts(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        RequestUtil.isValidPageAndSize(page,size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ContactDTO> contacts = contactService.getContactsDTO(pageable);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a contact by ID", description = "Get a contact by its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = Contact.class)))
    @ApiResponse(responseCode = "404", description = "Contact not found")
    public ResponseEntity<ContactDTO> getContactById(
            @Parameter(description = "Contact ID") @PathVariable Long id) {
        ContactDTO contact = contactService.getContactDTOById(id);
        return ResponseEntity.ok(contact);
    }

    @PostMapping
    @Operation(summary = "Create a new contact", description = "Create a new contact")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = Contact.class)))
    public ResponseEntity<ContactDTO> createContact(
            @Parameter(description = "Contact to create") @Valid @RequestBody ContactDTO contact) {
        ContactDTO newContact = contactService.createContact(contact);
        return ResponseEntity.ok(newContact);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a contact", description = "Update an existing contact")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = Contact.class)))
    @ApiResponse(responseCode = "404", description = "Contact not found")
    public ResponseEntity<ContactDTO> updateContact(
            @Parameter(description = "Contact ID") @PathVariable Long id,
            @Parameter(description = "Updated contact") @RequestBody ContactDTO contact) {
        ContactDTO updatedContact = contactService.updateContact(id, contact);
        return ResponseEntity.ok(updatedContact);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a contact", description = "Delete a contact by its ID")
    @ApiResponse(responseCode = "204", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Contact not found")
    public ResponseEntity<Void> deleteContact(
            @Parameter(description = "Contact ID") @PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search contacts", description = "Search contacts by term")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = Page.class)))
    public ResponseEntity<Page<ContactDTO>> searchContacts(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        log.debug("REST request to search contacts with term: {}", searchTerm);
        Pageable pageable = PageRequest.of(page, Math.min(size, 10));
        Page<ContactDTO> contacts = contactService.searchContacts(searchTerm, pageable);
        return ResponseEntity.ok(contacts);
    }
}


