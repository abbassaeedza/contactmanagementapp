package com.abbasza.contactapi.service;

import com.abbasza.contactapi.dto.CreateContactRequestDto;
import com.abbasza.contactapi.dto.GetContactResponseDto;
import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.ContactRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final ModelMapper modelMapper;

    @PreAuthorize("#username == authentication.principal.username")
    public Page<GetContactResponseDto> getAllContacts(String username, int page, int size) {
        User user = userService.findUserByUsername(username);
        Page<Contact> contacts = contactRepo.findContactsByUserId(user.getId(), PageRequest.of(page, size, Sort.by("firstName")));
        List<GetContactResponseDto> contactList = contacts.stream().map(contact -> modelMapper.map(contact, GetContactResponseDto.class)).toList();
        return new PageImpl<>(contactList);
    }

    @PreAuthorize("#username == authentication.principal.username")
    public GetContactResponseDto getContact(String username, UUID id) {
        User user = userService.findUserByUsername(username);
        Optional<Contact> contact = contactRepo.findContactByIdAndUserId(id, user.getId());
        if (contact.isPresent()) {
            return modelMapper.map(contact.get(), GetContactResponseDto.class);
        } else {
            log.info("Contact: {} Not Found", id);
            throw new EntityNotFoundException("Contact Not Found" + id);
        }
    }

    @PreAuthorize("#username == authentication.principal.username")
    public GetContactResponseDto saveContact(String username, CreateContactRequestDto createContactRequestDto) {
        try {
            log.info("Creating Contact for User: {}", username);
            User user = userService.findUserByUsername(username);
            Contact contact = Contact.builder()
                    .title(createContactRequestDto.getTitle())
                    .firstName(createContactRequestDto.getFirstName())
                    .lastName(createContactRequestDto.getFirstName())
                    .email(createContactRequestDto.getEmail())
                    .phone(createContactRequestDto.getPhone())
                    .build();
            contact.setUser(user);
            Contact savedContact = contactRepo.save(contact);
            return modelMapper.map(savedContact, GetContactResponseDto.class);
        } catch (Exception e) {
            log.error("Error occured while creating Contact for User: {}", username);
            throw new IllegalArgumentException(e);
        }
    }

    @PreAuthorize("#username == authentication.principal.username")
    public GetContactResponseDto updateContact(String username, UUID contactId, CreateContactRequestDto updateContactRequestDto) {
        try {
            log.info("Updating Contact: {}", contactId);
            User user = userService.findUserByUsername(username);
            Optional<Contact> contact = contactRepo.findContactByIdAndUserId(contactId, user.getId());
            if (contact.isPresent()) {
                contact.get().setFirstName((updateContactRequestDto.getFirstName() != null && !updateContactRequestDto.getFirstName().isEmpty()) ? updateContactRequestDto.getFirstName() : contact.get().getFirstName());
                contact.get().setLastName((updateContactRequestDto.getLastName() != null && !updateContactRequestDto.getLastName().isEmpty()) ? updateContactRequestDto.getLastName() : contact.get().getLastName());
                contact.get().setTitle((updateContactRequestDto.getTitle() != null && !updateContactRequestDto.getTitle().isEmpty()) ? updateContactRequestDto.getTitle() : contact.get().getTitle());
                contact.get().setEmail((updateContactRequestDto.getEmail() != null && !updateContactRequestDto.getEmail().isEmpty()) ? updateContactRequestDto.getEmail() : contact.get().getEmail());
                contact.get().setPhone((updateContactRequestDto.getPhone() != null && !updateContactRequestDto.getPhone().isEmpty()) ? updateContactRequestDto.getPhone() : contact.get().getPhone());
                return modelMapper.map(contact, GetContactResponseDto.class);
            } else {
                log.info("Contact: {} Not Found", contactId);
                throw new EntityNotFoundException("Contact " + contactId + " Not Found");
            }
        } catch (Exception e) {
            log.error("Error occured while updating Contact: {}", contactId);
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
                throw new EntityNotFoundException("Contact " + id + " Not Found");
            }
        } catch (Exception e) {
            log.error("Error occured while deleting Contact: {}", id);
            throw new EntityNotFoundException(e);
        }
    }
}
