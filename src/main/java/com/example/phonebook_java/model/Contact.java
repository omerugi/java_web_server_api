package com.example.phonebook_java.model;

import com.example.phonebook_java.model.enums.CountryCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contacts")
@NoArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_gen")
    @SequenceGenerator(name = "contact_gen", sequenceName = "contacts_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "country_code", nullable = false)
    private CountryCode countryCode = CountryCode.US;;

    @Column(name = "address")
    private String address;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Contact(Contact other) {
        this.id = other.id;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.phone = other.phone;
        this.countryCode = other.countryCode;
        this.address = other.address;
        this.createdAt = other.createdAt;
        this.updatedAt = other.updatedAt;
    }
}
