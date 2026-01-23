package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUser(@PathVariable String email){
        User user = userService.findUserByEmail(email);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/{email}")
    public ResponseEntity<User> updateUser(@RequestBody User updateUser, @PathVariable String email){
        User userInDB = userService.findUserByEmail(email);
        userInDB.setEmail((updateUser.getEmail() != null && !updateUser.getEmail().isEmpty()) ? updateUser.getEmail() : userInDB.getEmail());
        userInDB.setPhone((updateUser.getPhone() != null && !updateUser.getPhone().isEmpty()) ? updateUser.getPhone() : userInDB.getPhone());
        userInDB.setFirstName((updateUser.getFirstName() != null && !updateUser.getFirstName().isEmpty()) ? updateUser.getFirstName() : userInDB.getFirstName());
        userInDB.setLastName((updateUser.getLastName() != null && !updateUser.getLastName().isEmpty()) ? updateUser.getLastName() : userInDB.getLastName());
        if (updateUser.getPasswordHash() == null || updateUser.getPasswordHash().isEmpty()) {
            User user = userService.saveUser(userInDB);
            return ResponseEntity.ok().body(user);
        }else{
            userInDB.setPasswordHash(updateUser.getPasswordHash());
        }
        User user = userService.saveNewUser(userInDB); //encode password again
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable String email){
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }
}
