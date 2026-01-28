package com.abbasza.contactapi.service;

import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.ContactRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class ContactService {
    private final ContactRepo contactRepo;
    private final UserService userService;

    @PreAuthorize("#username == authentication.principal.username")
    public Page<Contact> getAllContacts(String username, int page, int size) {
        User user = userService.findUserByUsername(username);
        return contactRepo.findContactsByUserId(user.getId(), PageRequest.of(page, size, Sort.by("firstName")));
    }

    @PreAuthorize("#username == authentication.principal.username")
    public Contact getContactById(String username, UUID id) {
        User user = userService.findUserByUsername(username);
        Optional<Contact> contact = contactRepo.findContactByIdAndUserId(id, user.getId());
        return contact.orElseThrow(() -> {
            log.info("Contact: {} Not Found", id);
            return new EntityNotFoundException("Contact Not Found" + id);
        });
    }

    @PreAuthorize("#username == authentication.principal.username")
    public Contact saveContact(String username, Contact contact) {
        try {
            log.info("Creating Contact for User: {}", username);
            User user = userService.findUserByUsername(username);
            contact.setUser(user);
            return contactRepo.save(contact);
        } catch (Exception e) {
            log.error("Error occured while creating Contact: {}", contact.getId());
            throw new IllegalArgumentException(e);
        }
    }

    public void saveContact(Contact contact) {
        try {
            log.info("Updating Contact: {}", contact.getId());
            contactRepo.save(contact);
        } catch (Exception e) {
            log.error("Error occured while updating Contact: {}", contact.getId());
            throw new IllegalArgumentException(e);
        }
    }

    @PreAuthorize("#username == authentication.principal.username")
    public boolean deleteContactById(String username, UUID id) {
        try {
            log.info("Deleting Contact: {}", id);
            User user = userService.findUserByUsername(username);
            Optional<Contact> contact = contactRepo.findContactByIdAndUserId(id, user.getId());
            if (contact.isPresent()) {
                contactRepo.deleteById(id);
                return true;
            } else {
                log.info("Contact: {} Not Found", id);
                throw new EntityNotFoundException();
            }
        } catch (Exception e) {
            log.error("Error occured while deleting Contact: {}", id);
            throw new EntityNotFoundException(e);
        }
    }

    public void deleteContactById(UUID id) {
        try {
            log.info("Deleting Contact: {}", id);
            contactRepo.deleteById(id);
        } catch (Exception e) {
            log.error("Error occured while deleting Contact: {}", id);
            throw new RuntimeException(e);
        }
    }
}
