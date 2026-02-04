package com.abbasza.contactapi.service;

import com.abbasza.contactapi.dto.CreateContactRequestDto;
import com.abbasza.contactapi.dto.GetContactResponseDto;
import com.abbasza.contactapi.model.Contact;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.ContactRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepo contactRepo;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ContactService contactService;

    private User user;
    private Contact contact;
    private final UUID USER_ID = UUID.randomUUID();
    private final UUID CONTACT_ID = UUID.randomUUID();
    private final String USERNAME = "test@mail.com";

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(USER_ID)
                .email(USERNAME)
                .build();

        contact = Contact.builder()
                .id(CONTACT_ID)
                .user(user)
                .firstName("John")
                .lastName("Smith")
                .build();
    }

    // ========== GET CONTACT ==========

    @Test
    void getContact_shouldReturnContact() {
        when(userService.findUserByUsername(USERNAME))
                .thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(CONTACT_ID, USER_ID))
                .thenReturn(Optional.of(contact));
        when(modelMapper.map(any(), eq(GetContactResponseDto.class)))
                .thenReturn(new GetContactResponseDto());

        GetContactResponseDto result =
                contactService.getContact(USERNAME, CONTACT_ID);

        assertNotNull(result);
    }

    @Test
    void getContact_shouldThrowIfNotFound() {
        when(userService.findUserByUsername(USERNAME))
                .thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(CONTACT_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> contactService.getContact(USERNAME, CONTACT_ID));
    }

    // ========== SAVE CONTACT ==========

    @Test
    void saveContact_shouldPersistWithUser() {
        CreateContactRequestDto dto = new CreateContactRequestDto();
        dto.setFirstName("Jane");
        dto.setTitle("CEO");

        when(userService.findUserByUsername(USERNAME))
                .thenReturn(user);
        when(contactRepo.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(modelMapper.map(any(), eq(GetContactResponseDto.class)))
                .thenReturn(new GetContactResponseDto());

        contactService.saveContact(USERNAME, dto);

        verify(contactRepo).save(argThat(c ->
                c.getUser().equals(user) &&
                        c.getFirstName().equals("Jane")
        ));
    }

    // ========== UPDATE CONTACT ==========

    @Test
    void updateContact_shouldModifyFields() {
        CreateContactRequestDto dto = new CreateContactRequestDto();
        dto.setFirstName("Updated");

        when(userService.findUserByUsername(USERNAME))
                .thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(CONTACT_ID, USER_ID))
                .thenReturn(Optional.of(contact));
        when(modelMapper.map(any(), eq(GetContactResponseDto.class)))
                .thenReturn(new GetContactResponseDto());

        contactService.updateContact(USERNAME, CONTACT_ID, dto);

        assertEquals("Updated", contact.getFirstName());
    }

    // ========== DELETE CONTACT ==========

    @Test
    void deleteContact_shouldDelete() {
        when(userService.findUserByUsername(USERNAME))
                .thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(CONTACT_ID, USER_ID))
                .thenReturn(Optional.of(contact));

        boolean result =
                contactService.deleteContactById(USERNAME, CONTACT_ID);

        assertTrue(result);
        verify(contactRepo).deleteById(CONTACT_ID);
    }

    @Test
    void deleteContact_shouldFailIfNotOwned() {
        when(userService.findUserByUsername(USERNAME))
                .thenReturn(user);
        when(contactRepo.findContactByIdAndUserId(CONTACT_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> contactService.deleteContactById(USERNAME, CONTACT_ID));
    }
}

