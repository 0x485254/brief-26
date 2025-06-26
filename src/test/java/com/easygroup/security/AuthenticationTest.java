package com.easygroup.security;

import com.easygroup.dto.AuthRequest;
import com.easygroup.dto.AuthResponse;
import com.easygroup.entity.User;
import com.easygroup.repository.UserRepository;
import com.easygroup.service.AuthService;
import com.easygroup.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for authentication functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthenticationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    public void setup() {
        // Clean up any existing test user
        userRepository.findByEmail(TEST_EMAIL).ifPresent(user -> userRepository.delete(user));
    }

    @Test
    public void testRegisterUser() {
        // Register a new user
        User user = authService.register(TEST_EMAIL, TEST_PASSWORD, "Test", "User");

        // Verify user was created
        assertNotNull(user);
        assertEquals(TEST_EMAIL, user.getEmail());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, user.getPassword()));
        assertEquals(User.Role.USER, user.getRole());
        assertTrue(user.getIsActivated());
    }

    @Test
    public void testAuthenticateUser() {
        // Register a new user
        authService.register(TEST_EMAIL, TEST_PASSWORD, "Test", "User");

        // Authenticate the user
        AuthResponse response = authService.authenticate(TEST_EMAIL, TEST_PASSWORD);

        // Verify authentication response
        assertNotNull(response);
        assertEquals(TEST_EMAIL, response.getEmail());
        assertNotNull(response.getToken());
        assertEquals("USER", response.getRole());

        // Verify token is valid
        String token = response.getToken();
        String username = jwtService.extractUsername(token);
        assertEquals(TEST_EMAIL, username);
    }

    @Test
    public void testAuthenticateWithInvalidCredentials() {
        // Register a new user
        authService.register(TEST_EMAIL, TEST_PASSWORD, "Test", "User");

        // Try to authenticate with wrong password
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(TEST_EMAIL, "wrongpassword");
        });

        // Try to authenticate with non-existent user
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate("nonexistent@example.com", TEST_PASSWORD);
        });
    }

    @Test
    public void testChangePassword() {
        // Register a new user
        User user = authService.register(TEST_EMAIL, TEST_PASSWORD, "Test", "User");

        // Change password
        String newPassword = "newpassword123";
        User updatedUser = authService.changePassword(user, newPassword);

        // Verify password was changed
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));

        // Verify can authenticate with new password
        AuthResponse response = authService.authenticate(TEST_EMAIL, newPassword);
        assertNotNull(response);
        assertEquals(TEST_EMAIL, response.getEmail());
    }
}