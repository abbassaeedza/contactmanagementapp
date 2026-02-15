package com.abbasza.contactapi.service;

import com.abbasza.contactapi.dto.ChangePassRequestDto;
import com.abbasza.contactapi.dto.GetSelfResponseDto;
import com.abbasza.contactapi.dto.UpdateUserRequestDto;
import com.abbasza.contactapi.dto.UpdateUserResponseDto;
import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("#username == authentication.principal.username")
    public GetSelfResponseDto getUser(String username) {
        User user = findUserByUsername(username);
        return modelMapper.map(user, GetSelfResponseDto.class);
    }

    @PreAuthorize("#username == authentication.principal.username")
    public UpdateUserResponseDto updateUser(String username, UpdateUserRequestDto updateUserRequestDto) {
        try {
            log.info("Updating USER: {}", username);
            User userInDB = findUserByUsername(username);

            userInDB.setEmail(updateUserRequestDto.getEmail());
            userInDB.setPhone(updateUserRequestDto.getPhone());

            userInDB.setFirstName((updateUserRequestDto.getFirstname() != null && !updateUserRequestDto.getFirstname().isEmpty()) ? updateUserRequestDto.getFirstname() : userInDB.getFirstName());
            userInDB.setLastName((updateUserRequestDto.getLastname() != null && !updateUserRequestDto.getLastname().isEmpty()) ? updateUserRequestDto.getLastname() : userInDB.getLastName());

            return modelMapper.map(userRepo.save(userInDB), UpdateUserResponseDto.class);
        } catch (Exception e) {
            log.error("Error occured while updating USER: {}", username);
            throw new IllegalArgumentException(e);
        }
    }

    @PreAuthorize("#username == authentication.principal.username")
    public boolean changePassword(String username, ChangePassRequestDto changePassRequestDto) throws AuthException {
        try {
            log.info("Changing Passowrd for USER: {}", username);
            User userInDB = findUserByUsername(username);
            String oldPassInDB = userInDB.getPassword();
            String newPassHash = passwordEncoder.encode(changePassRequestDto.getNewpassword());
            if (passwordEncoder.matches(changePassRequestDto.getOldpassword(), oldPassInDB)) {
                userInDB.setPassword(newPassHash);
                userRepo.save(userInDB);
                return true;
            } else {
                throw new AuthException("Incorrect Password");
            }
        } catch (Exception e) {
            log.error("Incorrect Password for USER: {}", username);
            throw new AuthException(e);
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

    public void updateUser(User user) {
        userRepo.save(user);
    }
}
