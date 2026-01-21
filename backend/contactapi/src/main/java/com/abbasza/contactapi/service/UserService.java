package com.abbasza.contactapi.service;

import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class UserService {
    @Autowired
    private ContactService contactService;
    @Autowired
    private UserRepo userRepo;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public User saveNewUser(User user){
        try{
            log.info("Creating User: {}", user.getUserName());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedTime(LocalDateTime.now());
            return userRepo.save(user);
        } catch (Exception e) {
            log.error("Error occured while creating USER: {}", user.getUserName());
            throw new RuntimeException(e);
        }
    }

    public User saveUser(User user){
        try{
            log.info("Updating USER: {}", user.getUserName());
            return userRepo.save(user);
        }catch (Exception e){
            log.error("Error occured while updating USER: {}", user.getUserName());
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(String username){
        try {
            log.info("Deleting USER: {}", username);
            User user = findUserByUserName(username);
            user.getContacts().forEach(x -> contactService.deleteContactById(x.getId()));
            userRepo.deleteById(user.getId());
        } catch (Exception e) {
            log.error("Error occured while deleting USER: {}", username);
            throw new RuntimeException(e);
        }
    }

    public User findUserByUserName(String username){
        return userRepo.findUserByUserName(username).orElseThrow(() -> {
            log.error("USER: {} not found", username);
            return new UsernameNotFoundException("Username not found " + username);
        });
    }
}
