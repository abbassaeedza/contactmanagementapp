package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.dto.ChangePassRequestDto;
import com.abbasza.contactapi.dto.GetSelfResponseDto;
import com.abbasza.contactapi.dto.UpdateUserRequestDto;
import com.abbasza.contactapi.dto.UpdateUserResponseDto;
import com.abbasza.contactapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class  UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<GetSelfResponseDto> getSelf(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        GetSelfResponseDto getSelfResponseDto = userService.getUser(username);
        return ResponseEntity.ok().body(getSelfResponseDto);
    }

    @PutMapping("/edit")
    public ResponseEntity<UpdateUserResponseDto> updateSelf(@RequestBody UpdateUserRequestDto updateUserRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UpdateUserResponseDto updateUserResponseDto = userService.updateUser(username, updateUserRequestDto);
        return ResponseEntity.ok().body(updateUserResponseDto);
    }

    @PutMapping("/change-password")
    public ResponseEntity<HttpStatus> changePassword(@RequestBody ChangePassRequestDto changePassRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean changed = userService.changePassword(username, changePassRequestDto);
        return changed ? ResponseEntity.ok().build() : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteSelf(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
