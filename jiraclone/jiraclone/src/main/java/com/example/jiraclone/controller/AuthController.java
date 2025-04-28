package com.example.jiraclone.controller;

import com.example.jiraclone.dto.LoginRequest;
import com.example.jiraclone.dto.RegisterRequest;
import com.example.jiraclone.dto.UserDto;
import com.example.jiraclone.service.AuthService;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.security.Principal;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
     // Inject SecurityContextRepository to save context after manual authentication
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();


    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            UserDto registeredUser = authService.register(registerRequest);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
       try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Explicitly save context to session
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

            // Return user details upon successful login
            UserDto userDto = authService.getUserByEmail(loginRequest.getEmail());
            return ResponseEntity.ok(userDto);

       } catch (Exception e) {
           // Log the exception e
           return new ResponseEntity<>("Login failed: Invalid credentials", HttpStatus.UNAUTHORIZED);
       }
    }

     // Endpoint to get current logged-in user info (useful for frontend)
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
             UserDto userDto = authService.getUserByEmail(principal.getName());
             return ResponseEntity.ok(userDto);
        } catch (Exception e) {
             // Log error
             return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Or internal server error
        }
    }

    // Logout is handled by SecurityConfig logout handler, but you can add a controller endpoint if needed
     @PostMapping("/logout")
     public ResponseEntity<?> logoutUser() {
         // Spring Security's logout handler configured in SecurityConfig should handle this.
         // This endpoint might just confirm the action or rely entirely on the filter chain.
         return ResponseEntity.ok("Logout successful");
     }
}