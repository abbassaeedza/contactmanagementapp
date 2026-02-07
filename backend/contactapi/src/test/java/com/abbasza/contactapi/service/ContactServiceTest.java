package com.abbasza.contactapi.service;

import com.abbasza.contactapi.dto.ContactDetailResponseDto;
import com.abbasza.contactapi.dto.ContactRequestDto;
import com.abbasza.contactapi.dto.ContactResponseDto;
import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.model.User;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@email.com")
                .contacts(new ArrayList<>())
                .build();
    }

    // ========== GET CONTACTS ==========

    @Test
    void getAllContacts_success() {
        Contact contact = Contact.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .user(user)
                .build();

        when(userService.findUserByUsername(anyString())).thenReturn(user);
        when(contactRepo.findContactsByUserId(eq(user.getId()), any()))
                .thenReturn(new PageImpl<>(List.of(contact)));
        when(modelMapper.map(any(), eq(ContactResponseDto.class)))
                .thenReturn(new ContactResponseDto());

        Page<ContactResponseDto> result =
                contactService.getAllContacts(user.getUsername(), 0, 10);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getContact_success() {
        UUID id = UUID.randomUUID();
        Contact contact = Contact.builder().id(id).user(user).build();

        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(id, user.getId()))
                .thenReturn(Optional.of(contact));
        when(modelMapper.map(any(), eq(ContactDetailResponseDto.class)))
                .thenReturn(new ContactDetailResponseDto());

        assertNotNull(contactService.getContact(user.getUsername(), id));
    }

    @Test
    void getContact_notFound() {
        UUID id = UUID.randomUUID();

        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(id, user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> contactService.getContact(user.getUsername(), id));
    }

    // ========== SEARCH CONTACTS ==========

    @Test
    void getSearchContacts_success() {
        Contact contact1 = Contact.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .user(user)
                .build();

        when(userService.findUserByUsername(anyString())).thenReturn(user);
        when(contactRepo.findContactByFirstNameOrLastName(user.getId(), "jo"))
                .thenReturn(List.of(contact1));
        when(modelMapper.map(any(Contact.class), eq(ContactResponseDto.class)))
                .thenReturn(new ContactResponseDto());

        List<ContactResponseDto> result =
                contactService.getSearchContacts(user.getUsername(), "jo");

        assertEquals(1, result.size());
        verify(contactRepo).findContactByFirstNameOrLastName(user.getId(), "jo");
    }

    // ========== UPDATE CONTACTS ==========

    @Test
    void updateContact_success() {
        UUID contactId = UUID.randomUUID();

        Contact existing = Contact.builder()
                .id(contactId)
                .firstName("Old")
                .lastName("Name")
                .user(user)
                .emails(new ArrayList<>())
                .phones(new ArrayList<>())
                .build();

        ContactRequestDto request = new ContactRequestDto();
        request.setFirstname("New");
        request.setLastname("Name");

        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(contactId, user.getId()))
                .thenReturn(Optional.of(existing));
        when(contactRepo.save(any(Contact.class))).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(Contact.class), eq(ContactDetailResponseDto.class)))
                .thenReturn(new ContactDetailResponseDto());

        ContactDetailResponseDto result =
                contactService.updateContact(user.getUsername(), contactId, request);

        assertNotNull(result);
        verify(contactRepo).save(existing);
    }

    @Test
    void updateContact_notFound() {
        UUID contactId = UUID.randomUUID();

        ContactRequestDto request = new ContactRequestDto();
        request.setFirstname("New");

        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(contactId, user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> contactService.updateContact(user.getUsername(), contactId, request));
    }

    // ========== DELETE CONTACTS ==========

    @Test
    void deleteContact_success() {
        UUID id = UUID.randomUUID();
        Contact contact = Contact.builder().id(id).user(user).build();

        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(id, user.getId()))
                .thenReturn(Optional.of(contact));

        assertTrue(contactService.deleteContactById(user.getUsername(), id));
        verify(contactRepo).deleteById(id);
    }

    @Test
    void deleteContact_notFound() {
        UUID id = UUID.randomUUID();

        when(userService.findUserByUsername(any())).thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(id, user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> contactService.deleteContactById(user.getUsername(), id));
    }
}
