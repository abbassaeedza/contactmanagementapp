package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.dto.ContactDetailResponseDto;
import com.abbasza.contactapi.dto.ContactRequestDto;
import com.abbasza.contactapi.dto.ContactResponseDto;
import com.abbasza.contactapi.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contact")
public class ContactController {
    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<Page<ContactResponseDto>> getAllContacts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", defaultValue = "10") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Page<ContactResponseDto> contactPage = contactService.getAllContacts(username, page, size);
        return ResponseEntity.ok().body(contactPage);
    }

    @GetMapping("/s")
    public ResponseEntity<List<ContactResponseDto>> getSearchContacts(@RequestParam(value = "query") String query) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<ContactResponseDto> searchContacts = contactService.getSearchContacts(username, query);
        return ResponseEntity.ok().body(searchContacts);
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<ContactDetailResponseDto> getContact(@PathVariable UUID contactId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            ContactDetailResponseDto contactDetailResponseDto = contactService.getContact(username, contactId);
            return ResponseEntity.ok().body(contactDetailResponseDto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ContactDetailResponseDto> createContact(@RequestBody ContactRequestDto contactRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ContactDetailResponseDto newContact = contactService.saveContact(username, contactRequestDto);
        return ResponseEntity.created(URI.create("/contact/" + newContact.getId())).body(newContact);
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<ContactDetailResponseDto> updateContact(@PathVariable UUID contactId, @RequestBody ContactRequestDto updateContactRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ContactDetailResponseDto contactDetailResponseDto = contactService.updateContact(username, contactId, updateContactRequestDto);
        return ResponseEntity.ok().body(contactDetailResponseDto);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<HttpStatus> deleteContact(@PathVariable UUID contactId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean removed = contactService.deleteContactById(username, contactId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
