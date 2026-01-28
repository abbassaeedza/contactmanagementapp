package com.abbasza.contactapi.service;

import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class UserService {
    private final ContactService contactService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, @Lazy ContactService contactService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.contactService = contactService;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveNewUser(User user) {
        try {
            log.info("Updating USER: {}", user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepo.save(user);
        } catch (Exception e) {
            log.error("Error occured while creating USER: {}", user.getUsername());
            throw new IllegalArgumentException(e);
        }
    }

    public User saveUser(User user) {
        try {
            log.info("Updating USER: {}", user.getUsername());
            return userRepo.save(user);
        } catch (Exception e) {
            log.error("Error occured while updating USER: {}", user.getUsername());
            throw new IllegalArgumentException(e);
        }
    }

    @PreAuthorize("#username == authentication.principal.username")
    public void deleteUser(String username) {
        try {
            log.info("Deleting USER: {}", username);
            User user = findUserByUsername(username);
            userRepo.deleteById(user.getId());
        } catch (Exception e) {
            log.error("Error occured while deleting USER: {}", username);
            throw new EntityNotFoundException(e);
        }
    }

    public User findUserByUsername(String username) {
        Optional<User> user = userRepo.findUserByEmail(username);
        if (user.isEmpty()) {
            user = userRepo.findUserByPhone(username);
        }
        return user.orElseThrow(() -> {
            log.error("USER: {} not found", username);
            return new UsernameNotFoundException("Username not found " + username);
        });
    }
}
