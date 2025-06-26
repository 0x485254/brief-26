package com.easygroup.controller;

import com.easygroup.dto.AuthRequest;
import com.easygroup.dto.AuthResponse;
import com.easygroup.entity.User;
import com.easygroup.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     *
     * @param request the registration request
     * @return the created user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRequest request) {
        try {
            User user = authService.register(request.getEmail(), request.getPassword(), null, null);
            
            // In a real application, you would generate a token here
            AuthResponse response = new AuthResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    "token_placeholder",
                    user.getRole().toString()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Authenticate a user.
     *
     * @param request the authentication request
     * @return the authenticated user
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        Optional<User> userOpt = authService.authenticate(request.getEmail(), request.getPassword());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // In a real application, you would generate a token here
            AuthResponse response = new AuthResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    "token_placeholder",
                    user.getRole().toString()
            );
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}