package com.abbasza.contactapi.service;

import com.abbasza.contactapi.dto.ChangePassRequestDto;
import com.abbasza.contactapi.dto.UpdateUserRequestDto;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.UserRepo;
import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private final UUID USER_ID = UUID.randomUUID();
    private final String EMAIL = "test@mail.com";
    private final String PHONE = "123456789";

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .phone(PHONE)
                .password("hashedPass")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    // ========== FIND USER ==========

    @Test
    void findUserByUsername_shouldFindByEmail() {
        when(userRepo.findUserByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        User result = userService.findUserByUsername(EMAIL);

        assertEquals(USER_ID, result.getId());
        verify(userRepo).findUserByEmail(EMAIL);
        verify(userRepo, never()).findUserByPhone(any());
    }

    @Test
    void findUserByUsername_shouldFallbackToPhone() {
        when(userRepo.findUserByEmail(PHONE))
                .thenReturn(Optional.empty());
        when(userRepo.findUserByPhone(PHONE))
                .thenReturn(Optional.of(user));

        User result = userService.findUserByUsername(PHONE);

        assertEquals(USER_ID, result.getId());
        verify(userRepo).findUserByPhone(PHONE);
    }

    @Test
    void findUserByUsername_shouldThrowIfNotFound() {
        when(userRepo.findUserByEmail(any()))
                .thenReturn(Optional.empty());
        when(userRepo.findUserByPhone(any()))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.findUserByUsername("missing"));
    }

    // ========== UPDATE USER ==========

    @Test
    void updateUser_shouldUpdateEmailWhenUserHasEmail() {
        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setEmail("new@mail.com");

        when(userRepo.findUserByEmail(EMAIL))
                .thenReturn(Optional.of(user));
        when(userRepo.existsUserByEmail(EMAIL))
                .thenReturn(true);
        when(userRepo.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        userService.updateUser(EMAIL, dto);

        assertEquals("new@mail.com", user.getEmail());
        verify(userRepo).save(user);
    }

    @Test
    void updateUser_shouldUpdatePhoneWhenUserHasNoEmail() {
        user.setEmail(null);

        UpdateUserRequestDto dto = new UpdateUserRequestDto();
        dto.setPhone("999");

        when(userRepo.findUserByPhone(PHONE))
                .thenReturn(Optional.of(user));
        when(userRepo.existsUserByEmail(PHONE))
                .thenReturn(false);
        when(userRepo.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        userService.updateUser(PHONE, dto);

        assertEquals("999", user.getPhone());
    }

    // ========== CHANGE PASSWORD ==========

    @Test
    void changePassword_shouldSucceed() throws AuthException {
        ChangePassRequestDto dto = new ChangePassRequestDto();
        dto.setOldpassword("old");
        dto.setNewpassword("new");

        when(userRepo.findUserByEmail(EMAIL))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "hashedPass"))
                .thenReturn(true);
        when(passwordEncoder.encode("new"))
                .thenReturn("newHash");

        boolean result = userService.changePassword(EMAIL, dto);

        assertTrue(result);
        assertEquals("newHash", user.getPassword());
        verify(userRepo).save(user);
    }

    @Test
    void changePassword_shouldFailOnWrongOldPassword() {
        ChangePassRequestDto dto = new ChangePassRequestDto();
        dto.setOldpassword("wrong");

        when(userRepo.findUserByEmail(EMAIL))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword(EMAIL, dto));
    }

    // ========== DELETE USER ==========

    @Test
    void deleteUser_shouldDeleteById() {
        when(userRepo.findUserByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        userService.deleteUser(EMAIL);

        verify(userRepo).deleteById(USER_ID);
    }
}
