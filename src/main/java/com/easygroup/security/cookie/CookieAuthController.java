package com.easygroup.security.cookie;

import com.easygroup.dto.AuthRequest;
import com.easygroup.dto.AuthResponse;
import com.easygroup.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for cookie-based authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth/cookie")
public class CookieAuthController {

    private final CookieAuthService cookieAuthService;

    @Autowired
    public CookieAuthController(CookieAuthService cookieAuthService) {
        this.cookieAuthService = cookieAuthService;
    }

    /**
     * Register a new user and set a JWT token cookie.
     *
     * @param request the registration request
     * @param response the HTTP response to set the cookie on
     * @return the created user details (without the token in the response body)
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody @Valid AuthRequest request,
            HttpServletResponse response) {
        try {
            User user = cookieAuthService.register(request.getEmail(), request.getPassword(), null, null);

            // Authenticate the user after registration to generate a token and set it as a cookie
            AuthResponse authResponse = cookieAuthService.authenticate(request.getEmail(), request.getPassword(), response);

            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Authenticate a user and set a JWT token cookie.
     *
     * @param request the authentication request
     * @param response the HTTP response to set the cookie on
     * @return the authenticated user details (without the token in the response body)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid AuthRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = cookieAuthService.authenticate(request.getEmail(), request.getPassword(), response);
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Logout a user by clearing the JWT token cookie.
     *
     * @param response the HTTP response to clear the cookie from
     * @return a success response
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieAuthService.logout(response);
        return ResponseEntity.ok().build();
    }
}