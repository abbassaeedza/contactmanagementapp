package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.dto.CreateContactRequestDto;
import com.abbasza.contactapi.dto.GetContactResponseDto;
import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contact")
public class ContactController {
    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<Page<GetContactResponseDto>> getAllContacts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Page<GetContactResponseDto> contactPage = contactService.getAllContacts(username, page, size);
        return ResponseEntity.ok().body(contactPage);
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<GetContactResponseDto> getContact(@PathVariable UUID contactId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            GetContactResponseDto getContactResponseDto = contactService.getContact(username, contactId);
            return ResponseEntity.ok().body(getContactResponseDto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<GetContactResponseDto> createContact(@RequestBody CreateContactRequestDto createContactRequestDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            GetContactResponseDto newContact = contactService.saveContact(username, createContactRequestDto);
            return ResponseEntity.created(URI.create("/contact/" + newContact.getId())).body(newContact);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<GetContactResponseDto> updateContact(@PathVariable UUID contactId, @RequestBody CreateContactRequestDto updateContactRequestDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            GetContactResponseDto getContactResponseDto = contactService.updateContact(username, contactId, updateContactRequestDto);
            return ResponseEntity.ok().body(getContactResponseDto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<HttpStatus> deleteContact(@PathVariable UUID contactId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean removed = contactService.deleteContactById(username, contactId);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
