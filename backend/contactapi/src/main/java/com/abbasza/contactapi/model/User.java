package com.abbasza.contactapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Table(name = "user", indexes = {
        @Index(name = "idx_userName", columnList = "userName")
})
public class User {
    @Id
    @UuidGenerator
    @Column(unique = true, updatable = false)
    private UUID id;
    @NonNull
    private String userName;
    @NonNull
    private String password;
    private String firstName;
    private String lastName;
    private LocalDateTime createdTime;
    @OneToMany
    private List<Contact> contacts = new ArrayList<>();
}
