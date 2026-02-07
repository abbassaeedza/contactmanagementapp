package com.abbasza.contactapi.model;

import com.abbasza.contactapi.model.type.EmailType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Table(
        name = "contact_email",
        indexes = {
                @Index(name = "idx_email_type", columnList = "email_type"),
                @Index(name = "idx_email_value", columnList = "email_value")
        }
)
public class ContactEmail {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_type", nullable = false, columnDefinition = "VARCHAR")
    private EmailType emailType;

    @Column(name = "email_value", nullable = false)
    private String emailValue;
}
