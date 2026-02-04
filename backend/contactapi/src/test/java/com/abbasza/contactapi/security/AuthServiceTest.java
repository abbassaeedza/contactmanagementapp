package com.abbasza.contactapi.security;

import com.abbasza.contactapi.dto.LoginRequestDto;
import com.abbasza.contactapi.dto.LoginResponseDto;
import com.abbasza.contactapi.dto.SignupRequestDto;
import com.abbasza.contactapi.dto.SignupResponseDto;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;
    private final UUID USER_ID = UUID.randomUUID();
    private final String EMAIL = "user@mail.com";
    private final String PASSWORD = "pass";

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .password("hashed")
                .build();
    }

    // ========== LOGIN ==========

    @Test
    void login_shouldAuthenticateAndReturnToken() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername(EMAIL);
        dto.setPassword(PASSWORD);

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(auth);
        when(auth.getPrincipal())
                .thenReturn(user);
        when(authUtil.generateAcessToken(user))
                .thenReturn("jwt-token");

        LoginResponseDto response = authService.login(dto);

        assertEquals(USER_ID, response.getUserId());
        assertEquals("jwt-token", response.getJwt());
    }

    @Test
    void login_shouldFailWhenAuthenticationFails() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername(EMAIL);
        dto.setPassword(PASSWORD);

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad creds"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(dto));
    }

    // ========== SIGNUP ==========

    @Test
    void signup_shouldCreateUserWithEmail() {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setEmail(EMAIL);
        dto.setPassword(PASSWORD);
        dto.setFirstName("John");

        when(userRepo.findUserByEmail(EMAIL))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD))
                .thenReturn("hash");
        when(userRepo.save(any()))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    u.setId(USER_ID);
                    return u;
                });

        SignupResponseDto response = authService.signup(dto);

        assertEquals(USER_ID, response.getUserId());
        assertEquals(EMAIL, response.getUsername());
    }

    @Test
    void signup_shouldCreateUserWithPhone() {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setPhone("12345");
        dto.setPassword(PASSWORD);

        when(userRepo.findUserByPhone("12345"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD))
                .thenReturn("hash");
        when(userRepo.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        SignupResponseDto response = authService.signup(dto);

        assertEquals("12345", response.getUsername());
    }

    @Test
    void signup_shouldFailWhenUserExists() {
        SignupRequestDto dto = new SignupRequestDto();
        dto.setEmail(EMAIL);

        when(userRepo.findUserByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> authService.signup(dto));
    }

    @Test
    void signup_shouldFailWhenUsernameMissing() {
        SignupRequestDto dto = new SignupRequestDto();

        assertThrows(IllegalArgumentException.class,
                () -> authService.signup(dto));
    }
}
