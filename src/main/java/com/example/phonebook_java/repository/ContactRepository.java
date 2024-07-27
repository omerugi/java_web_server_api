package com.example.phonebook_java.repository;

import com.example.phonebook_java.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query(value = "SELECT * FROM contacts WHERE " +
            "LOWER(first_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(last_name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))",
            nativeQuery = true)
    Page<Contact> searchContacts(@Param("searchTerm") String searchTerm, Pageable pageable);
}
