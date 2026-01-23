package com.abbasza.contactapi.service;

import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.ContactRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class ContactService {
    private final ContactRepo contactRepo;
    private final UserService userService;

    @Autowired
    public ContactService(ContactRepo contactRepo, UserService userService) {
        this.contactRepo = contactRepo;
        this.userService = userService;
    }

    @Value("${DB_DATABASE}")
    private String valuee;


    public Page<Contact> getAllContacts(int page, int size) {
        return contactRepo.findAll(PageRequest.of(page, size, Sort.by("firstName")));
    }

    public Contact getContactById(UUID id) {
        return contactRepo.findById(id).orElseThrow(() -> {
            log.info("Contact: {} Not Found", id);
            return new RuntimeException("Contact Not Found" + id);
        });
    }

    public Contact saveContact(Contact contact, String email) {
        try {
            Contact savedContact = contactRepo.save(contact);
            log.info("Creating Contact: {}", savedContact.getId());
            User user = userService.findUserByEmail(email);
            user.getContacts().add(savedContact);
            userService.saveUser(user);
            return savedContact;
        } catch (RuntimeException e) {
            log.error("Error occured while creating Contact: {}", contact.getId());
            throw new RuntimeException(e);
        }
    }

    public void saveContact(Contact contact) {
        try {
            log.info("Updating Contact: {}", contact.getId());
            contactRepo.save(contact);
        } catch (RuntimeException e) {
            log.error("Error occured while updating Contact: {}", contact.getId());
            throw new RuntimeException(e);
        }
    }

    public boolean deleteContactById(UUID id, String email) {
        try {
            log.info("Deleting Contact: {}", id);
            User user = userService.findUserByEmail(email);
            boolean removed = user.getContacts().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveUser(user);
                contactRepo.deleteById(id);
            }else{
                log.info("Contact Not Found: {}", id);
            }
            return removed;
        } catch (Exception e) {
            log.error("Error occured while deleting Contact: {}", id);
            throw new RuntimeException(e);
        }
    }
}
