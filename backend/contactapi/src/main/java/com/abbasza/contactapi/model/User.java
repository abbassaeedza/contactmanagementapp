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
