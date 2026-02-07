package com.abbasza.contactapi.service;

import com.abbasza.contactapi.dto.*;
import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.model.ContactEmail;
import com.abbasza.contactapi.model.ContactPhone;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.model.type.EmailType;
import com.abbasza.contactapi.model.type.PhoneType;
import com.abbasza.contactapi.repository.ContactEmailRepo;
import com.abbasza.contactapi.repository.ContactPhoneRepo;
import com.abbasza.contactapi.repository.ContactRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @InjectMocks
    private ContactService contactService;

    @Mock
    private ContactRepo contactRepo;
    @Mock
    private ContactEmailRepo contactEmailRepo;
    @Mock
    private ContactPhoneRepo contactPhoneRepo;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;

    private User user;
    private Contact contact;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@email.com")
                .contacts(new ArrayList<>())
                .build();

        contact = Contact.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .user(user)
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();
    }

    // ========== GET CONTACTS ==========

    @Test
    void getAllContacts_success() {
        Page<Contact> page =
                new PageImpl<>(List.of(contact));

        when(userService.findUserByUsername(anyString())).thenReturn(user);
        when(contactRepo.findContactsByUserId(eq(user.getId()), any(PageRequest.class)))
                .thenReturn(page);
        when(modelMapper.map(any(Contact.class), eq(ContactResponseDto.class)))
                .thenReturn(new ContactResponseDto());

        Page<ContactResponseDto> result =
                contactService.getAllContacts(user.getUsername(), 0, 10);

        assertEquals(1, result.getTotalElements());
        verify(contactRepo).findContactsByUserId(eq(user.getId()), any());
    }


    @Test
    void getContact_success() {
        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(contact.getId(), user.getId()))
                .thenReturn(Optional.of(contact));
        when(modelMapper.map(any(Contact.class), eq(ContactDetailResponseDto.class)))
                .thenReturn(new ContactDetailResponseDto());

        ContactDetailResponseDto dto =
                contactService.getContact(user.getUsername(), contact.getId());

        assertNotNull(dto);
    }


    @Test
    void getContact_notFound() {
        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> contactService.getContact(user.getUsername(), UUID.randomUUID()));
    }


    // ========== SEARCH CONTACTS ==========

    @Test
    void getSearchContacts_success() {
        when(userService.findUserByUsername(anyString())).thenReturn(user);
        when(contactRepo.findContactByFirstNameOrLastName(user.getId(), "jo"))
                .thenReturn(List.of(contact));
        when(modelMapper.map(any(Contact.class), eq(ContactResponseDto.class)))
                .thenReturn(new ContactResponseDto());

        List<ContactResponseDto> result =
                contactService.getSearchContacts(user.getUsername(), "jo");

        assertEquals(1, result.size());
        verify(contactRepo).findContactByFirstNameOrLastName(user.getId(), "jo");
    }

    // ========== SAVE CONTACTS ==========

    @Test
    void saveContact_withoutEmailsAndPhones() {
        ContactRequestDto request = new ContactRequestDto();
        request.setFirstname("John");

        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.save(any(Contact.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(ContactDetailResponseDto.class)))
                .thenReturn(new ContactDetailResponseDto());

        ContactDetailResponseDto result =
                contactService.saveContact(user.getUsername(), request);

        assertNotNull(result);
        verify(contactEmailRepo, never()).save(any());
        verify(contactPhoneRepo, never()).save(any());
    }

    @Test
    void saveContact_success_withEmailsAndPhones() {
        ContactRequestDto request = new ContactRequestDto();
        request.setFirstname("John");

        ContactEmailDto emailDto = new ContactEmailDto();
        emailDto.setEmailtype(EmailType.WORK);
        emailDto.setEmailvalue("john@work.com");

        ContactPhoneDto phoneDto = new ContactPhoneDto();
        phoneDto.setPhonetype(PhoneType.PERSONAL);
        phoneDto.setPhonevalue("123");

        request.setEmails(List.of(emailDto));
        request.setPhones(List.of(phoneDto));

        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.save(any(Contact.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(contactEmailRepo.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        when(contactPhoneRepo.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(Contact.class), eq(ContactDetailResponseDto.class)))
                .thenReturn(new ContactDetailResponseDto());

        ContactDetailResponseDto dto =
                contactService.saveContact(user.getUsername(), request);

        assertNotNull(dto);
        verify(contactRepo, atLeastOnce()).save(any());
        verify(userService).updateUser(user);
    }

    @Test
    void saveContactEmail_whenEmailListIsNull() {
        contact.setEmails(null);

        ContactEmailDto dto = new ContactEmailDto();
        dto.setEmailtype(EmailType.WORK);
        dto.setEmailvalue("a@b.com");

        when(contactEmailRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Contact result =
                contactService.saveContactEmail(contact, List.of(dto));

        assertEquals(1, result.getEmails().size());
    }

    @Test
    void saveContact_exceptionThrown() {
        when(userService.findUserByUsername(any()))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(IllegalArgumentException.class,
                () -> contactService.saveContact(user.getUsername(), new ContactRequestDto()));
    }

    // ========== UPDATE CONTACTS ==========

    @Test
    void updateContact_withEmailAndPhoneCleanup() {
        UUID id = contact.getId();

        ContactEmail oldEmail = ContactEmail.builder().id(UUID.randomUUID()).build();
        ContactPhone oldPhone = ContactPhone.builder().id(UUID.randomUUID()).build();

        ContactRequestDto request = new ContactRequestDto();
        request.setEmails(List.of(new ContactEmailDto()));
        request.setPhones(List.of(new ContactPhoneDto()));

        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(id, user.getId()))
                .thenReturn(Optional.of(contact));
        when(contactEmailRepo.findContactEmailsByContactId(id))
                .thenReturn(List.of(oldEmail));
        when(contactPhoneRepo.findContactPhoneByContactId(id))
                .thenReturn(List.of(oldPhone));
        when(contactEmailRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(contactPhoneRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(contactRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(ContactDetailResponseDto.class)))
                .thenReturn(new ContactDetailResponseDto());

        contactService.updateContact(user.getUsername(), id, request);

        verify(contactEmailRepo).deleteById(oldEmail.getId());
        verify(contactPhoneRepo).deleteById(oldPhone.getId());
    }


    @Test
    void updateContact_notFound_branch() {
        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> contactService.updateContact(
                        user.getUsername(), UUID.randomUUID(), new ContactRequestDto()));
    }

    @Test
    void updateContactName_nullAndEmptyFields() {
        ContactRequestDto request = new ContactRequestDto();
        request.setFirstname("");
        request.setLastname(null);
        request.setTitle("");

        Contact updated =
                invokeUpdateContactName(request, contact);

        assertEquals("John", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
    }

    private Contact invokeUpdateContactName(ContactRequestDto req, Contact c) {
        try {
            Method m = ContactService.class
                    .getDeclaredMethod("updateContactName", ContactRequestDto.class, Contact.class);
            m.setAccessible(true);
            return (Contact) m.invoke(null, req, c);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // ========== DELETE CONTACTS ==========

    @Test
    void deleteContact_success() {
        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(contact.getId(), user.getId()))
                .thenReturn(Optional.of(contact));

        boolean result =
                contactService.deleteContactById(user.getUsername(), contact.getId());

        assertTrue(result);
        verify(contactRepo).deleteById(contact.getId());
    }


    @Test
    void deleteContact_notFound() {
        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> contactService.deleteContactById(
                        user.getUsername(), UUID.randomUUID()));
    }
}