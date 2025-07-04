package com.easygroup.controller;

import com.easygroup.dto.AuthResponse;
import com.easygroup.dto.LoginRequest;
import com.easygroup.dto.RegisterRequest;
import com.easygroup.entity.User;
import com.easygroup.service.AuthService;
import com.easygroup.service.JwtService;
import com.easygroup.service.MailingService;
import com.easygroup.service.UserService;

import jakarta.mail.MessagingException;
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
        private MailingService mailingService;

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
                ReflectionTestUtils.setField(authController, "mailFrom", "no-reply@easygroup.com");

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
        void register_Success() throws MessagingException {
                when(authService.register(anyString(), anyString(), anyString(), anyString())).thenReturn(testUser);
                when(jwtService.generateValidationToken(testUser)).thenReturn("validation-token");

                ResponseEntity<Boolean> result = authController.registerWithMail(registerRequest, response);

                assertEquals(HttpStatus.CREATED, result.getStatusCode());
                assertTrue(result.getBody());
                verify(authService).register("messi@barca.com", "password123", "Lionel", "Messi");
                verify(jwtService).generateValidationToken(testUser);
                verify(mailingService).sendHtmlEmail(anyString(), anyString(), anyString(), anyString());
        }

        @Test
        void register_EmailAlreadyExists_ReturnsConflict() {
                when(authService.register(anyString(), anyString(), anyString(), anyString()))
                                .thenThrow(new IllegalArgumentException("Email already exists"));

                ResponseEntity<Boolean> result = authController.registerWithMail(registerRequest, response);

                assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
                assertNull(result.getBody());
        }

        @Test
        void register_EmailSendingFails_StillReturnsSuccess() throws MessagingException {
                doThrow(new MessagingException("Simulated failure")).when(mailingService)
                                .sendHtmlEmail(anyString(), anyString(), anyString(), anyString());

                when(authService.register(anyString(), anyString(), anyString(), anyString())).thenReturn(testUser);
                when(jwtService.generateValidationToken(testUser)).thenReturn("validation-token");

                ResponseEntity<Boolean> result = authController.registerWithMail(registerRequest, response);

                assertEquals(HttpStatus.CREATED, result.getStatusCode());
                assertTrue(result.getBody());
                verify(authService).register("messi@barca.com", "password123", "Lionel", "Messi");
                verify(jwtService).generateValidationToken(testUser);
                verify(mailingService).sendHtmlEmail(anyString(), anyString(), anyString(), anyString());
        }

        @Test
        void verifyEmail_Success_RedirectsToLoginPage() {
                String validationToken = "valid-token-for-user";
                doNothing().when(authService).verifyAccount(validationToken);

                ResponseEntity<?> result = authController.verifyEmail(validationToken);

                assertEquals(HttpStatus.FOUND, result.getStatusCode());
                assertTrue(result.getHeaders().containsKey("Location"));
                assertEquals("https://brief-react-v3-groupshuffle-11e877.gitlab.io/#/login",
                                result.getHeaders().get("Location").get(0));
                verify(authService).verifyAccount(validationToken);
        }

        @Test
        void verifyEmail_InvalidToken_RedirectsToErrorPage() {
                String validationToken = "invalid-token";
                doThrow(new RuntimeException("Invalid or expired token")).when(authService)
                                .verifyAccount(validationToken);

                ResponseEntity<?> result = authController.verifyEmail(validationToken);

                assertEquals(HttpStatus.FOUND, result.getStatusCode());
                assertTrue(result.getHeaders().containsKey("Location"));
                assertEquals("http://localhost:8080/verification-failed", result.getHeaders().get("Location").get(0));
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
                assertEquals(authResponse.getEmail(), result.getBody().getEmail());
                assertEquals(authResponse.getFirstName(), result.getBody().getFirstName());
                assertEquals(authResponse.getLastName(), result.getBody().getLastName());
                assertEquals(authResponse.getRole(), result.getBody().getRole());
                assertEquals(authResponse.getIsActivated(), result.getBody().getIsActivated());
                assertEquals(authResponse.getUserId(), result.getBody().getUserId());
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
                verify(authService).authenticate("messi@barca.com", "password123", response);
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
