package com.abbasza.contactapi.repository;

import com.abbasza.contactapi.model.Contact;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepo extends JpaRepository<Contact, UUID> {
    Optional<Contact> findContactsByFirstName(String firstName, Sort sort);
    Optional<Contact> findContactbyEmail(String email);
}
