package com.abbasza.contactapi.repository;

import com.abbasza.contactapi.model.Contact;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepo extends JpaRepository<Contact, UUID> {
}
