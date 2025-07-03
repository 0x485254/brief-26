package com.easygroup.controller;

import com.easygroup.dto.AuthResponse;
import com.easygroup.dto.LoginRequest;
import com.easygroup.dto.RegisterRequest;
import com.easygroup.entity.User;
import com.easygroup.service.AuthService;
import com.easygroup.service.JwtService;
import com.easygroup.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

        @Mock
        private AuthService authService;

        @Mock
        private JwtService jwtService;

        @Mock
        private UserService userService;

        @Mock
        private HttpServletResponse response;

        @InjectMocks
        private AuthController authController;

        private RegisterRequest registerRequest;
        private LoginRequest loginRequest;
        private User testUser;
        private AuthResponse authResponse;

        @BeforeEach
        void setUp() {
                ReflectionTestUtils.setField(authController, "smtpServer", "smtp.test.com");
                ReflectionTestUtils.setField(authController, "smtpUsername", "test@test.com");
                ReflectionTestUtils.setField(authController, "smtpPassword", "password");
                ReflectionTestUtils.setField(authController, "applicationUrl", "http://localhost:8080");

                registerRequest = new RegisterRequest();
                registerRequest.setEmail("messi@barca.com");
                registerRequest.setPassword("password123");
                registerRequest.setFirstName("Lionel");
                registerRequest.setLastName("Messi");

                loginRequest = new LoginRequest();
                loginRequest.setEmail("messi@barca.com");
                loginRequest.setPassword("password123");

                testUser = new User();
                testUser.setId(UUID.randomUUID());
                testUser.setEmail("messi@barca.com");
                testUser.setFirstName("Lionel");
                testUser.setLastName("Messi");
                testUser.setIsActivated(true);
                testUser.setRole(User.Role.USER);

                authResponse = new AuthResponse();
                authResponse.setUserId(testUser.getId());
                authResponse.setEmail("messi@barca.com");
                authResponse.setFirstName("Lionel");
                authResponse.setLastName("Messi");
                authResponse.setRole("USER");
                authResponse.setIsActivated(true);
        }

        @Test
        void register_Success() {
                when(authService.register(anyString(), anyString(), anyString(), anyString())).thenReturn(testUser);
                when(jwtService.generateValidationToken(testUser)).thenReturn("validation-token");

                ResponseEntity<Boolean> result = authController.register(registerRequest, response);

                assertEquals(HttpStatus.CREATED, result.getStatusCode());
                assertEquals(true, result.getBody());
                verify(authService).register("messi@barca.com", "password123", "Lionel", "Messi");
                verify(jwtService).generateValidationToken(testUser);
        }

        @Test
        void register_EmailAlreadyExists_ReturnsConflict() {
                when(authService.register(anyString(), anyString(), anyString(), anyString()))
                                .thenThrow(new IllegalArgumentException("Email already exists"));

                ResponseEntity<Boolean> result = authController.register(registerRequest, response);

                assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
                assertNull(result.getBody());
        }

        @Test
        void register_EmailSendingFails_StillReturnsSuccess() {

                when(authService.register(anyString(), anyString(), anyString(), anyString())).thenReturn(testUser);
                when(jwtService.generateValidationToken(testUser)).thenReturn("validation-token");

                ReflectionTestUtils.setField(authController, "smtpServer", "invalid-smtp");

                ResponseEntity<Boolean> result = authController.register(registerRequest, response);

                assertEquals(HttpStatus.CREATED, result.getStatusCode());
                assertEquals(true, result.getBody());
                verify(authService).register("messi@barca.com", "password123", "Lionel", "Messi");
        }

        @Test
        void verifyEmail_Success() {
                String validationToken = "I'm a Valid Toookn Thou";
                doNothing().when(authService).verifyAccount(validationToken);

                ResponseEntity<Boolean> result = authController.verifyEmail(validationToken);

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertEquals(true, result.getBody());
                verify(authService).verifyAccount(validationToken);
        }

        @Test
        void login_Success() {
                when(userService.findByEmail("messi@barca.com")).thenReturn(Optional.of(testUser));
                when(authService.authenticate(anyString(), anyString(), any(HttpServletResponse.class)))
                                .thenReturn(authResponse);

                ResponseEntity<AuthResponse> result = authController.login(loginRequest, response);

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertNotNull(result.getBody());
                assertEquals("messi@barca.com", result.getBody().getEmail());
                assertEquals("Lionel", result.getBody().getFirstName());
                assertEquals("Messi", result.getBody().getLastName());
                assertEquals("USER", result.getBody().getRole());
                verify(authService).authenticate("messi@barca.com", "password123", response);
        }

        @Test
        void login_UserNotFound_ReturnsUnauthorized() {
                when(userService.findByEmail("messi@barca.com")).thenReturn(Optional.empty());

                ResponseEntity<AuthResponse> result = authController.login(loginRequest, response);

                assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
                assertNull(result.getBody());
                verify(authService, never()).authenticate(anyString(), anyString(), any());
        }

        @Test
        void login_UserNotActivated_ReturnsUnauthorized() {
                testUser.setIsActivated(false);
                when(userService.findByEmail("messi@barca.com")).thenReturn(Optional.of(testUser));

                ResponseEntity<AuthResponse> result = authController.login(loginRequest, response);

                assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
                assertNull(result.getBody());
                verify(authService, never()).authenticate(anyString(), anyString(), any());
        }

        @Test
        void login_WrongPassword_ReturnsUnauthorized() {
                when(userService.findByEmail("messi@barca.com")).thenReturn(Optional.of(testUser));
                when(authService.authenticate(anyString(), anyString(), any(HttpServletResponse.class)))
                                .thenThrow(new AuthenticationException("Invalid credentials") {
                                });

                ResponseEntity<AuthResponse> result = authController.login(loginRequest, response);

                assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
                assertNull(result.getBody());
        }

        @Test
        void logout_Success() {
                doNothing().when(authService).logout(response);

                ResponseEntity<Void> result = authController.logout(response);

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertNull(result.getBody());
                verify(authService).logout(response);
        }
}