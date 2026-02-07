package com.abbasza.contactapi.repository;

import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("dev")
class ContactRepoTest {

    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    private TestEntityManager em;

    @Test
    void findContactByIdAndUserId_success() {
        User user = em.persist(User.builder().email("a@b.com").password("x").build());

        Contact contact = em.persist(
                Contact.builder().firstName("John").user(user).build()
        );

        Optional<Contact> found =
                contactRepo.findContactByIdAndUserId(contact.getId(), user.getId());

        assertTrue(found.isPresent());
    }

    @Test
    void findContactsByUserId() {
        User user = em.persist(User.builder().email("c@d.com").password("x").build());

        em.persist(Contact.builder().firstName("A").user(user).build());
        em.persist(Contact.builder().firstName("B").user(user).build());

        Page<Contact> page =
                contactRepo.findContactsByUserId(user.getId(), PageRequest.of(0, 10));

        assertEquals(2, page.getContent().size());
    }
}
