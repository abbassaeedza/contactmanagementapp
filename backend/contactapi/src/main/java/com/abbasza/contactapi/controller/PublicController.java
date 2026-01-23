package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {
    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck(){
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@RequestBody User user){
        userService.saveUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
