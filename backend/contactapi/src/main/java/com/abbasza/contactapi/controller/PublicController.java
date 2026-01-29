package com.abbasza.contactapi.controller;

import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
public class PublicController {
    private final UserService userService;

    @Profile("!prod")
    @GetMapping("/")
    public ResponseEntity<String> healthCheck(){
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

//    @PostMapping("/create-user")
//    public ResponseEntity<User> createUser(@RequestBody User user){
//        try {
//            User newUser = userService.saveNewUser(user);
//            return ResponseEntity.ok().body(newUser);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
}
