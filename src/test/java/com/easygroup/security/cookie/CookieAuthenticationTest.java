package com.easygroup.security.cookie;

import com.easygroup.dto.AuthRequest;
import com.easygroup.entity.User;
import com.easygroup.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for cookie-based authentication functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CookieAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CookieService cookieService;

    private static final String TEST_EMAIL = "cookie-test@example.com";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    public void setup() {
        // Clean up any existing test user
        userRepository.findByEmail(TEST_EMAIL).ifPresent(user -> userRepository.delete(user));

        // Create a test user
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(TEST_EMAIL);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        user.setFirstName("Cookie");
        user.setLastName("Test");
        user.setIsActivated(true);
        user.setCguDate(LocalDate.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole(User.Role.USER);
        userRepository.save(user);
    }

    @Test
    public void testLoginWithCookieAuth() throws Exception {
        // Create login request
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(TEST_EMAIL);
        authRequest.setPassword(TEST_PASSWORD);

        // Perform login request
        MvcResult result = mockMvc.perform(post("/api/auth/cookie/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + TEST_EMAIL + "\",\"password\":\"" + TEST_PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(cookieService.getCookieName()))
                .andExpect(cookie().httpOnly(cookieService.getCookieName(), true))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.token").doesNotExist()) // Token should not be in response body
                .andReturn();

        // Verify cookie is set
        String cookie = result.getResponse().getCookie(cookieService.getCookieName()).getValue();
        assertNotNull(cookie);
        assertTrue(cookie.length() > 0);
    }

    @Test
    public void testLogout() throws Exception {
        // First login to get a cookie
        mockMvc.perform(post("/api/auth/cookie/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + TEST_EMAIL + "\",\"password\":\"" + TEST_PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(cookieService.getCookieName()));

        // Then logout and verify cookie is cleared
        mockMvc.perform(post("/api/auth/cookie/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge(cookieService.getCookieName(), 0));
    }

    @Test
    public void testRegisterWithCookieAuth() throws Exception {
        String newEmail = "new-cookie-user@example.com";
        
        // Create register request
        mockMvc.perform(post("/api/auth/cookie/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + newEmail + "\",\"password\":\"" + TEST_PASSWORD + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(cookie().exists(cookieService.getCookieName()))
                .andExpect(cookie().httpOnly(cookieService.getCookieName(), true))
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.token").doesNotExist()); // Token should not be in response body

        // Verify user was created
        assertTrue(userRepository.existsByEmail(newEmail));
    }
}