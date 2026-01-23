package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.service.ContactService;
import com.abbasza.contactapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/contact")
public class ContactController {
    private final ContactService contactService;
    private final UserService userService;

    @Autowired
    public ContactController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<Contact>> getAllContacts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Contact> contactPage = contactService.getAllContacts(page, size);
        return ResponseEntity.ok().body(contactPage);
    }

    @GetMapping("/id/{contactId}")
    public ResponseEntity<Contact> getContact(@PathVariable UUID contactId) {
        try {
            Contact contact = contactService.getContactById(contactId);
            return ResponseEntity.ok().body(contact);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{email}")
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact, @PathVariable String email) {
        try {
            Contact newContact = contactService.saveContact(contact, email);
            return ResponseEntity.created(URI.create("/v1/contact/id/" + newContact.getId())).body(newContact);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("{email}/id/{contactId}")
    public ResponseEntity<Contact> updateContact(@RequestBody Contact updateContact, @PathVariable UUID contactId, @PathVariable String email) {
        User user = userService.findUserByEmail(email);
        List<Contact> contactList = user.getContacts().stream().filter(x -> x.getId().equals(contactId)).toList();
        if (!contactList.isEmpty()) {
            Contact oldContact = contactService.getContactById(contactId);
            oldContact.setFirstName((updateContact.getFirstName() != null && !updateContact.getFirstName().isEmpty()) ? updateContact.getFirstName() : oldContact.getFirstName());
            oldContact.setLastName((updateContact.getLastName() != null && !updateContact.getLastName().isEmpty()) ? updateContact.getLastName() : oldContact.getLastName());
            oldContact.setTitle((updateContact.getTitle() != null && !updateContact.getTitle().isEmpty()) ? updateContact.getTitle() : oldContact.getTitle());
            oldContact.setEmail((updateContact.getEmail() != null && !updateContact.getEmail().isEmpty()) ? updateContact.getEmail() : oldContact.getEmail());
            oldContact.setPhone((updateContact.getPhone() != null && !updateContact.getPhone().isEmpty()) ? updateContact.getPhone() : oldContact.getPhone());
            contactService.saveContact(oldContact);
            return ResponseEntity.ok().body(oldContact);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("{email}/id/{contactId}")
    public ResponseEntity<HttpStatus> deleteContact(@PathVariable UUID contactId, @PathVariable String email) {
        boolean removed = contactService.deleteContactById(contactId, email);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
