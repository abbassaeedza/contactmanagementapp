package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.dto.LoginRequestDto;
import com.abbasza.contactapi.dto.LoginResponseDto;
import com.abbasza.contactapi.dto.SignupRequestDto;
import com.abbasza.contactapi.dto.SignupResponseDto;
import com.abbasza.contactapi.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.ok().body(authService.login(loginRequestDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto){
        SignupResponseDto newUser = authService.signup(signupRequestDto);
        return ResponseEntity.created(URI.create("/user")).body(newUser);
    }
}
