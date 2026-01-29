package com.abbasza.contactapi.model;

import com.abbasza.contactapi.model.type.EmailType;
import com.abbasza.contactapi.model.type.PhoneType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.UuidGenerator;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Table(name = "contacts" ,indexes = {
        @Index(name= "idx_firstname_lastname", columnList = "firstName, lastName")
})
public class Contact {
    @Id
    @UuidGenerator
    @Column(unique = true, updatable = false, nullable = false)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String firstName;
    private String lastName;
    private String title;
    @ElementCollection()
    @CollectionTable(
            name = "contact_email",
            joinColumns = @JoinColumn(name = "contact_id")
    )
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "email_type", columnDefinition = "VARCHAR")
    @Column(name = "email_value", nullable = false)
    private Map<EmailType, String> email = new EnumMap<>(EmailType.class);
    @ElementCollection()
    @CollectionTable(
            name = "contact_phone",
            joinColumns = @JoinColumn(name = "contact_id")
    )
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "phone_type", columnDefinition = "VARCHAR")
    @Column(name = "phone_value", nullable = false)
    private Map<PhoneType, String> phone = new EnumMap<>(PhoneType.class);
}
