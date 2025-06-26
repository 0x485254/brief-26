package com.easygroup.controller;

import com.easygroup.dto.AuthRequest;
import com.easygroup.dto.AuthResponse;
import com.easygroup.entity.User;
import com.easygroup.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

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
     * @return the created user with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRequest request) {
        try {
            User user = authService.register(request.getEmail(), request.getPassword(), null, null);

            // Authenticate the user after registration to generate a token
            AuthResponse response = authService.authenticate(request.getEmail(), request.getPassword());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Authenticate a user.
     *
     * @param request the authentication request
     * @return the authenticated user with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        try {
            AuthResponse response = authService.authenticate(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
