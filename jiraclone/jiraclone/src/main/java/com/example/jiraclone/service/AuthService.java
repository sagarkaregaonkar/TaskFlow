package com.example.jiraclone.service;

import com.example.jiraclone.dto.RegisterRequest;
import com.example.jiraclone.dto.UserDto;
import com.example.jiraclone.entity.User;
import com.example.jiraclone.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // Ensure atomicity
    public UserDto register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EntityExistsException("Email is already taken!");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToUserDto(savedUser);
    }

     // Login is handled by Spring Security's filter chain based on UserDetailsService
     // We might need a method to get current user info after login

    public UserDto getUserByEmail(String email) {
         User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email)); // Or UsernameNotFoundException
         return mapToUserDto(user);
    }


     private UserDto mapToUserDto(User user) {
         UserDto dto = new UserDto();
         dto.setId(user.getId());
         dto.setName(user.getName());
         dto.setEmail(user.getEmail());
         return dto;
     }
}