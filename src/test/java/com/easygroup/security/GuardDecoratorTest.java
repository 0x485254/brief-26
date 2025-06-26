package com.easygroup.security;

import com.easygroup.entity.User;
import com.easygroup.repository.UserRepository;
import com.easygroup.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the guard decorators.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class GuardDecoratorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final String USER_EMAIL = "user@example.com";
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String PASSWORD = "password123";

    private String userToken;
    private String adminToken;

    @BeforeEach
    public void setup() {
        // Clean up any existing test users
        userRepository.findByEmail(USER_EMAIL).ifPresent(user -> userRepository.delete(user));
        userRepository.findByEmail(ADMIN_EMAIL).ifPresent(user -> userRepository.delete(user));

        // Create a regular user
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(USER_EMAIL);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setFirstName("Regular");
        user.setLastName("User");
        user.setIsActivated(true);
        user.setCguDate(LocalDate.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole(User.Role.USER);
        userRepository.save(user);

        // Create an admin user
        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(PASSWORD));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setIsActivated(true);
        admin.setCguDate(LocalDate.now());
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);

        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(USER_EMAIL);
        userToken = jwtService.generateToken(userDetails);

        UserDetails adminDetails = userDetailsService.loadUserByUsername(ADMIN_EMAIL);
        adminToken = jwtService.generateToken(adminDetails);
    }

    @Test
    public void testPublicEndpoint() throws Exception {
        // Public endpoint should be accessible without authentication
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + USER_EMAIL + "\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAuthenticatedEndpoint() throws Exception {
        // Create a test endpoint with @IsAuthenticated
        // This is a simulation as we don't have a real endpoint with this annotation yet
        // In a real application, you would test against an actual endpoint

        // Unauthenticated request should be rejected
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/profile"))
                .andExpect(status().isUnauthorized());

        // Authenticated request with user token should be accepted
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/profile")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound()); // Not found because the endpoint doesn't exist, but authentication passed
    }

    @Test
    public void testAdminEndpoint() throws Exception {
        // Create a test endpoint with @IsAdmin
        // This is a simulation as we don't have a real endpoint with this annotation yet
        // In a real application, you would test against an actual endpoint

        // Unauthenticated request should be rejected
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/users"))
                .andExpect(status().isUnauthorized());

        // Authenticated request with user token should be forbidden
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/users")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        // Authenticated request with admin token should be accepted
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound()); // Not found because the endpoint doesn't exist, but authentication passed
    }
}