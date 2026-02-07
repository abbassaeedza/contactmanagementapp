package com.abbasza.contactapi.model;

import com.abbasza.contactapi.model.type.PhoneType;
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
        name = "contact_phone",
        indexes = {
                @Index(name = "idx_phone_type", columnList = "phone_type"),
                @Index(name = "idx_phone_value", columnList = "phone_value")
        }
)
public class ContactPhone {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "phone_type", nullable = false, columnDefinition = "VARCHAR")
    private PhoneType phoneType;

    @Column(name = "phone_value", nullable = false)
    private String phoneValue;
}
