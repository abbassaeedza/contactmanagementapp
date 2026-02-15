package com.abbasza.contactapi.service;

import com.abbasza.contactapi.dto.*;
import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.model.ContactEmail;
import com.abbasza.contactapi.model.ContactPhone;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.ContactEmailRepo;
import com.abbasza.contactapi.repository.ContactPhoneRepo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class ContactService {
    private final ContactRepo contactRepo;
    private final ContactEmailRepo contactEmailRepo;
    private final ContactPhoneRepo contactPhoneRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @PreAuthorize("#username == authentication.principal.username")
    public Page<ContactResponseDto> getAllContacts(String username, int page, int size) {
        User user = userService.findUserByUsername(username);
        Page<Contact> contacts = contactRepo.findContactsByUserId(user.getId(), PageRequest.of(page, size, Sort.by("firstName")));
        return contacts.map(contact -> modelMapper.map(contact, ContactResponseDto.class));
    }

    @PreAuthorize("#username == authentication.principal.username")
    public List<ContactResponseDto> getSearchContacts(String username, String query) {
        User user = userService.findUserByUsername(username);
        List<Contact> contacts = contactRepo.findContactByFirstNameOrLastName(user.getId(), query);
        return contacts.stream().map(contact -> modelMapper.map(contact, ContactResponseDto.class)).toList();
    }

    @PreAuthorize("#username == authentication.principal.username")
    public ContactDetailResponseDto getContact(String username, UUID id) {
        User user = userService.findUserByUsername(username);
        Optional<Contact> contact = contactRepo.findContactByIdAndUserId(id, user.getId());
        if (contact.isPresent()) {
            return modelMapper.map(contact.get(), ContactDetailResponseDto.class);
        } else {
            log.info("Contact: {} Not Found", id);
            throw new EntityNotFoundException("Contact Not Found " + id);
        }
    }

    @PreAuthorize("#username == authentication.principal.username")
    public ContactDetailResponseDto saveContact(String username, ContactRequestDto contactRequestDto) {
        try {
            log.info("Creating Contact for User: {}", username);
            User user = userService.findUserByUsername(username);
            Contact contact = Contact.builder()
                    .title(contactRequestDto.getTitle())
                    .firstName(contactRequestDto.getFirstname())
                    .lastName(contactRequestDto.getLastname())
                    .build();

            contact.setUser(user);
            Contact savedContact = contactRepo.save(contact);

            if (contactRequestDto.getEmails() != null && !contactRequestDto.getEmails().isEmpty()) {
                contact = saveContactEmail(savedContact, contactRequestDto.getEmails());
                savedContact = contactRepo.save(contact);
            }
            if (contactRequestDto.getPhones() != null && !contactRequestDto.getPhones().isEmpty()) {
                contact = saveContactPhone(savedContact, contactRequestDto.getPhones());
                savedContact = contactRepo.save(contact);
            }

            user.getContacts().add(savedContact);
            userService.updateUser(user);
            return modelMapper.map(savedContact, ContactDetailResponseDto.class);
        } catch (Exception e) {
            log.error("Error occured while creating Contact for User: {}", username);
            throw new IllegalArgumentException(e);
        }
    }

    @PreAuthorize("#username == authentication.principal.username")
    public ContactDetailResponseDto updateContact(String username, UUID contactId, ContactRequestDto contactRequestDto) {
        try {
            log.info("Updating Contact: {}", contactId);
            User user = userService.findUserByUsername(username);
            Optional<Contact> optionalContact = contactRepo.findContactByIdAndUserId(contactId, user.getId());
            if (optionalContact.isPresent()) {
                Contact contact = updateContactName(contactRequestDto, optionalContact.get());

                if (contactRequestDto.getEmails() != null && !contactRequestDto.getEmails().isEmpty()) {
                    for (ContactEmail dbEmail : contactEmailRepo.findContactEmailsByContactId(contactId)) {
                        contactEmailRepo.deleteById(dbEmail.getId());
                    }
                    contact = saveContactEmail(contact, contactRequestDto.getEmails());
                }

                if (contactRequestDto.getPhones() != null && !contactRequestDto.getPhones().isEmpty()) {
                    for (ContactPhone dbPhone : contactPhoneRepo.findContactPhoneByContactId(contactId)) {
                        contactPhoneRepo.deleteById(dbPhone.getId());
                    }
                    contact = saveContactPhone(contact, contactRequestDto.getPhones());
                }

                Contact savedContact = contactRepo.save(contact);
                return modelMapper.map(savedContact, ContactDetailResponseDto.class);
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

    private static Contact updateContactName(ContactRequestDto contactRequestDto, Contact contact) {
        contact.setFirstName((contactRequestDto.getFirstname() != null && !contactRequestDto.getFirstname().isEmpty()) ? contactRequestDto.getFirstname() : contact.getFirstName());
        contact.setLastName((contactRequestDto.getLastname() != null && !contactRequestDto.getLastname().isEmpty()) ? contactRequestDto.getLastname() : contact.getLastName());
        contact.setTitle((contactRequestDto.getTitle() != null && !contactRequestDto.getTitle().isEmpty()) ? contactRequestDto.getTitle() : contact.getTitle());
        return contact;
    }

    public Contact saveContactEmail(Contact contact, List<ContactEmailDto> requestEmails) {
        log.info("Saving Contact Emails for contactid: {}", contact.getId());
        List<ContactEmail> emailList = new ArrayList<>();
        for (ContactEmailDto requestEmail : requestEmails) {
            ContactEmail email = ContactEmail.builder()
                    .emailType(requestEmail.getEmailtype())
                    .emailValue(requestEmail.getEmailvalue())
                    .build();
            email.setContact(contact);
            ContactEmail savedEmail = contactEmailRepo.save(email);
            emailList.add(savedEmail);
        }
        if (contact.getEmails() == null) {
            contact.setEmails(emailList);
        } else {
            contact.getEmails().clear();
            contact.getEmails().addAll(emailList);
        }
        return contact;
    }

    public Contact saveContactPhone(Contact contact, List<ContactPhoneDto> requestPhones) {
        log.info("Saving Contact Phones for contactid: {}", contact.getId());
        List<ContactPhone> phoneList = new ArrayList<>();
        for (ContactPhoneDto requestPhone : requestPhones) {
            ContactPhone phone = ContactPhone.builder()
                    .phoneType(requestPhone.getPhonetype())
                    .phoneValue(requestPhone.getPhonevalue())
                    .build();
            phone.setContact(contact);
            ContactPhone savedPhone = contactPhoneRepo.save(phone);
            phoneList.add(savedPhone);
        }
        if (contact.getPhones() == null) {
            contact.setPhones(phoneList);
        } else {
            contact.getPhones().clear();
            contact.getPhones().addAll(phoneList);
        }
        return contact;
    }
}
