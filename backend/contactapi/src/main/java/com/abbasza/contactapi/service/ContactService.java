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
    private UserService userService;

    @Autowired
    public ContactService(ContactRepo contactRepo) {
        this.contactRepo = contactRepo;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Value("${DB_DATABASE}")
    private String valuee;

    public Page<Contact> getAllContacts(int page, int size){
        return contactRepo.findAll(PageRequest.of(page, size, Sort.by("firstName")));
    }

    public Contact getContactById(UUID id){
        return contactRepo.findById(id).orElseThrow(() -> {
            log.error("Contact: {} Not Found", id);
            return new RuntimeException("Contact Not Found" + id);
        });
    }

    public Contact saveContact(Contact contact, String userName){
        try{
            log.info("Creating Contact: {}", contact.getId());
            Contact savedContact = contactRepo.save(contact);
            User user = userService.findUserByUserName(userName);
            user.getContacts().add(savedContact);
            userService.saveUser(user);
            return savedContact;
        } catch (RuntimeException e) {
            log.error("Error occured while creating Contact: {}", contact.getId());
            throw new RuntimeException(e);
        }
    }

    public void deleteContactById(UUID id){
        contactRepo.deleteById(id);
    }
}
