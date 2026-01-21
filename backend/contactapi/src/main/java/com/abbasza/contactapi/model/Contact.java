package com.abbasza.contactapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Table(name = "contacts" ,indexes = {
        @Index(name= "idx_email", columnList = "email"),
        @Index(name= "idx_firstname_lastname", columnList = "firstName, lastName")
})
public class Contact {
    @Id
    @UuidGenerator
    @Column(unique = true, updatable = false)
    private UUID id;
    private String firstName;
    private String lastName;
    private String title;
    @Column(unique = true)
    @ElementCollection
    private Map<String, String> email = new HashMap<>();
    @Column(unique = true)
    @ElementCollection
    private Map<String, String> phone = new HashMap<>();
}
