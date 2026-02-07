package com.abbasza.contactapi.repository;

import com.abbasza.contactapi.model.ContactEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactEmailRepo extends JpaRepository<ContactEmail, UUID> {

    List<ContactEmail> findContactEmailsByContactId(UUID contactId);
}
