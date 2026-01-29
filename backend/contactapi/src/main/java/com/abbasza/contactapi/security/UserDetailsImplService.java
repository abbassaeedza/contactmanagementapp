package com.abbasza.contactapi.security;

import com.abbasza.contactapi.model.User;
import com.abbasza.contactapi.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsImplService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findUserByEmail(username);
        if (user.isEmpty()) {
            user = userRepo.findUserByPhone(username);
        }
        return user.orElseThrow(() -> new UsernameNotFoundException("User with " + username + " Not Found!"));
    }
}
