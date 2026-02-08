package com.abbasza.contactapi.security;

import com.abbasza.contactapi.dto.LoginRequestDto;
import com.abbasza.contactapi.dto.LoginResponseDto;
import com.abbasza.contactapi.dto.SignupRequestDto;
import com.abbasza.contactapi.dto.SignupResponseDto;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.UserRepo;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        log.info("Logging In USER: {}", loginRequestDto.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );
        User user = (User) authentication.getPrincipal();

        String token = authUtil.generateAcessToken(user);

        return new LoginResponseDto(user.getId(), token);
    }

    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        User user;
        boolean emailEmpty = signupRequestDto.getEmail() == null || signupRequestDto.getEmail().isEmpty();
        boolean phoneEmpty = signupRequestDto.getPhone() == null || signupRequestDto.getPhone().isEmpty();

        if (phoneEmpty && emailEmpty) {
            log.error("Empty Username");
            throw new IllegalArgumentException("Username empty");
        }

        if (!emailEmpty) {
            log.info("Creating USER: {}", signupRequestDto.getEmail());
            signupRequestDto.setPhone(null);
            user = userRepo.findUserByEmail(signupRequestDto.getEmail()).orElse(null);
        } else {
            log.info("Creating USER: {}", signupRequestDto.getPhone());
            signupRequestDto.setEmail(null);
            user = userRepo.findUserByPhone(signupRequestDto.getPhone()).orElse(null);
        }

        if (user != null) {
            log.info("USER: {} Already Exists", user.getUsername());
            throw new EntityExistsException("User already exists");
        }

        user = userRepo.save(User.builder()
                .createdTime(LocalDateTime.now())
                .firstName(signupRequestDto.getFirstname())
                .lastName(signupRequestDto.getLastname())
                .email(signupRequestDto.getEmail())
                .phone(signupRequestDto.getPhone())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .build());

        return new SignupResponseDto(user.getId(), user.getUsername());
    }
}
