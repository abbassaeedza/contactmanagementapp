package com.abbasza.contactapi.repository;

import com.abbasza.contactapi.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepo extends JpaRepository<Contact, UUID> {
    Page<Contact> findContactsByUserId(UUID userId, Pageable pageable);

    Optional<Contact> findContactByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT c FROM Contact c INNER JOIN User u ON c.user.id = u.id WHERE u.id = ?1 AND LOWER(c.firstName) LIKE %?2% OR LOWER(c.lastName) LIKE %?2% ORDER BY c.firstName LIMIT 10")
    List<Contact> findContactByFirstNameOrLastName(UUID userId, String query);
}
