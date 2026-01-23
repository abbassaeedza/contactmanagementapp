package com.abbasza.contactapi.service;

import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.ContactRepo;
import com.abbasza.contactapi.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class UserService {
    private final ContactService contactService;
    private final UserRepo userRepo;

//    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepo userRepo, @Lazy ContactService contactService) {
        this.userRepo = userRepo;
        this.contactService = contactService;
    }

    public User saveNewUser(User user) {
        try {
            log.info("Creating USER: {}", user.getEmail());
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedTime(LocalDateTime.now());
            return userRepo.save(user);
        } catch (Exception e) {
            log.error("Error occured while creating USER: {}", user.getEmail());
            throw new RuntimeException(e);
        }
    }

    public User saveUser(User user) {
        try {
            log.info("Updating USER: {}", user.getEmail());
            return userRepo.save(user);
        } catch (Exception e) {
            log.error("Error occured while updating USER: {}", user.getEmail());
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(String email) {
        try {
            log.info("Deleting USER: {}", email);
            User user = findUserByEmail(email);
            user.getContacts().forEach(x -> contactService.deleteContactById(x.getId()));
            userRepo.deleteById(user.getId());
        } catch (Exception e) {
            log.error("Error occured while deleting USER: {}", email);
            throw new RuntimeException(e);
        }
    }

    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email).orElseThrow(() -> {
            log.error("USER: {} not found", email);
            return new RuntimeException();
//            return new UsernameNotFoundException("Username not found " + email);
        });
    }
}
