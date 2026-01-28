package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.service.ContactService;
import com.abbasza.contactapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contact")
public class ContactController {
    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<Page<Contact>> getAllContacts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Page<Contact> contactPage = contactService.getAllContacts(username, page, size);
        return ResponseEntity.ok().body(contactPage);
    }

    @GetMapping("/id/{contactId}")
    public ResponseEntity<Contact> getContact(@PathVariable UUID contactId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            Contact contact = contactService.getContactById(username, contactId);
            return ResponseEntity.ok().body(contact);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Contact newContact = contactService.saveContact(username, contact);
            return ResponseEntity.created(URI.create("/contact/id/" + newContact.getId())).body(newContact);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/id/{contactId}")
    public ResponseEntity<Contact> updateContact(@RequestBody Contact newContact, @PathVariable UUID contactId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Contact contact = contactService.getContactById(username, contactId);
        if (contact != null) {
            contact.setFirstName((newContact.getFirstName() != null && !newContact.getFirstName().isEmpty()) ? newContact.getFirstName() : contact.getFirstName());
            contact.setLastName((newContact.getLastName() != null && !newContact.getLastName().isEmpty()) ? newContact.getLastName() : contact.getLastName());
            contact.setTitle((newContact.getTitle() != null && !newContact.getTitle().isEmpty()) ? newContact.getTitle() : contact.getTitle());
            contact.setEmail((newContact.getEmail() != null && !newContact.getEmail().isEmpty()) ? newContact.getEmail() : contact.getEmail());
            contact.setPhone((newContact.getPhone() != null && !newContact.getPhone().isEmpty()) ? newContact.getPhone() : contact.getPhone());
            contactService.saveContact(contact);
            return ResponseEntity.ok().body(contact);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/id/{contactId}")
    public ResponseEntity<HttpStatus> deleteContact(@PathVariable UUID contactId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean removed = contactService.deleteContactById(username, contactId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
