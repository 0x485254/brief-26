package com.easygroup.controller;

import com.easygroup.dto.AuthResponse;
import com.easygroup.entity.User;
import com.easygroup.service.AuthService;
import com.easygroup.service.JwtService;
import com.easygroup.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService authService;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private UserService userService;

        @Test
        void shouldUserRegister() throws Exception {
                User savedUser = createTestUser();
                AuthResponse authResponse = createAuthResponse(savedUser);

                when(authService.register(anyString(), anyString(), anyString(), anyString()))
                                .thenReturn(savedUser);
                when(authService.authenticate(anyString(), anyString(), any()))
                                .thenReturn(authResponse);

                String requestBody = """
                                {
                                    "email": "dodo@dodo.com",
                                    "password": "dodo1234",
                                    "firstName": "Dodo",
                                    "lastName": "Dodo"
                                }
                                """;

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.email").value("dodo@dodo.com"))
                                .andExpect(jsonPath("$.firstName").value("Dodo"))
                                .andExpect(jsonPath("$.lastName").value("Dodo"))
                                .andExpect(jsonPath("$.role").value("USER"))
                                .andExpect(jsonPath("$.isActivated").value(true));

                verify(authService).register("dodo@dodo.com", "dodo1234", "Dodo", "Dodo");
                verify(authService).authenticate(eq("dodo@dodo.com"), eq("dodo1234"), any());

                System.out.println("User Dodo registered successfully - welcome to the party!");
        }

        @Test
        void shouldRejectRegistrationWhenEmailAlreadyExists() throws Exception {
                when(authService.register(anyString(), anyString(), anyString(), anyString()))
                                .thenThrow(new IllegalArgumentException("Email already in use"));

                String requestBody = """
                                {
                                    "email": "dodo@dodo.com",
                                    "password": "dodo1234",
                                    "firstName": "Dodo",
                                    "lastName": "Dodo"
                                }
                                """;

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isConflict());

                verify(authService).register("dodo@dodo.com", "dodo1234", "Dodo", "Dodo");
                verifyNoMoreInteractions(authService);

                System.out.println("Dodo tried to register twice - nice try buddy!");
        }

        @Test
        void shouldRejectRegistrationWithInvalidEmail() throws Exception {
                String requestBody = """
                                {
                                    "email": "What-The F- Is This",
                                    "password": "Isthisevenanemail",
                                    "firstName": "Chris",
                                    "lastName": "Brown"
                                }
                                """;

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(authService);
                System.out.println("Chris learned that emails need @ symbols - education complete!");
        }

        @Test
        void shouldRejectRegistrationWithMissingFields() throws Exception {
                String requestBody = """
                                {
                                    "email": "Whyyyy@Meeeeee.com",
                                    "password": "SomePassword"
                                }
                                """;

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(authService);
                System.out.println("Anonymous person forgot their name - forms are hard!");
        }

        @Test
        void shouldAuthenticateUserSuccessfully() throws Exception {
                User existingUser = createTestUser();
                AuthResponse authResponse = createAuthResponse(existingUser);

                when(authService.authenticate(anyString(), anyString(), any()))
                                .thenReturn(authResponse);

                String requestBody = """
                                {
                                    "email": "dodo@dodo.com",
                                    "password": "NowThisIsACorrectPassword"
                                }
                                """;

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("dodo@dodo.com"))
                                .andExpect(jsonPath("$.role").value("USER"))
                                .andExpect(jsonPath("$.isActivated").value(true));

                verify(authService).authenticate(eq("dodo@dodo.com"), eq("NowThisIsACorrectPassword"), any());
                System.out.println("Dodo logged in successfully - password game is strong!");
        }

        @Test
        void shouldRejectLoginWithWrongCredentials() throws Exception {
                when(authService.authenticate(anyString(), anyString(), any()))
                                .thenThrow(new AuthenticationException("Bad credentials") {
                                });

                String requestBody = """
                                {
                                    "email": "dodo@dodo.com",
                                    "password": "IsThisEvenTheCorrectPassword"
                                }
                                """;

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isUnauthorized());

                verify(authService).authenticate(eq("dodo@dodo.com"), eq("IsThisEvenTheCorrectPassword"), any());
                System.out.println("Dodo forgot their password - happens to the best of us!");
        }

        @Test
        void shouldRejectLoginWithMissingCredentials() throws Exception {
                String requestBody = """
                                {
                                    "email": "dodo@dodo.com"
                                }
                                """;

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(authService);
                System.out.println("Dodo forgot their password field - memory issues confirmed!");
        }

        @Test
        void shouldLogoutUserSuccessfully() throws Exception {
                doNothing().when(authService).logout(any());

                mockMvc.perform(post("/api/auth/logout"))
                                .andExpect(status().isOk());

                verify(authService).logout(any());
                System.out.println("Dodo logged out successfully - see you later alligator!");
        }

        @Test
        void shouldHandleLogoutWithoutAuthentication() throws Exception {
                doNothing().when(authService).logout(any());

                mockMvc.perform(post("/api/auth/logout"))
                                .andExpect(status().isOk());

                verify(authService).logout(any());
                System.out.println("Anonymous logout handled gracefully - no harm, no foul!");
        }

        private User createTestUser() {
                User user = new User();
                user.setId(UUID.randomUUID());
                user.setEmail("dodo@dodo.com");
                user.setFirstName("Dodo");
                user.setLastName("Dodo");
                user.setRole(User.Role.USER);
                user.setIsActivated(true);
                return user;
        }

        private AuthResponse createAuthResponse(User user) {
                return new AuthResponse(
                                user.getId(),
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName(),
                                null,
                                user.getRole().toString(),
                                user.getIsActivated());
        }
}