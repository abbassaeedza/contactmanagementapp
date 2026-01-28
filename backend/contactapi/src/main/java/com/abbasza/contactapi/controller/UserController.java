package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<User> getSelf(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findUserByUsername(username);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping
    public ResponseEntity<User> updateSelf(@RequestBody User updateUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userInDB = userService.findUserByUsername(username);
        userInDB.setEmail((updateUser.getEmail() != null && !updateUser.getEmail().isEmpty()) ? updateUser.getEmail() : userInDB.getEmail());
        userInDB.setPhone((updateUser.getPhone() != null && !updateUser.getPhone().isEmpty()) ? updateUser.getPhone() : userInDB.getPhone());
        userInDB.setFirstName((updateUser.getFirstName() != null && !updateUser.getFirstName().isEmpty()) ? updateUser.getFirstName() : userInDB.getFirstName());
        userInDB.setLastName((updateUser.getLastName() != null && !updateUser.getLastName().isEmpty()) ? updateUser.getLastName() : userInDB.getLastName());
        if (updateUser.getPassword() == null || updateUser.getPassword().isEmpty()) {
            User user = userService.saveUser(userInDB);
            return ResponseEntity.ok().body(user);
        }else{
            userInDB.setPassword(updateUser.getPassword());
        }
        User user = userService.saveNewUser(userInDB); //encode password again
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteSelf(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
