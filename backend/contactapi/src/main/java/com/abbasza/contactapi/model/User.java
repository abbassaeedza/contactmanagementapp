package com.abbasza.contactapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Table(name = "app_user", indexes = {
        @Index(name = "idx_email", columnList = "email")
})
public class User {
    @Id
    @UuidGenerator
    @Column(unique = true, updatable = false, nullable = false)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true)
    private String phone;
    @Column(nullable = false)
    private String passwordHash;
    private String firstName;
    private String lastName;
    private LocalDateTime createdTime;
    @OneToMany
    private List<Contact> contacts = new ArrayList<>();
}
