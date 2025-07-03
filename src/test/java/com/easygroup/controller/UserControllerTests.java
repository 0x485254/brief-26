package com.easygroup.controller;

import com.easygroup.dto.AuthResponse;
import com.easygroup.dto.UserUpdateRequest;
import com.easygroup.entity.User;
import com.easygroup.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

        @Mock
        private UserService userService;

        @InjectMocks
        private UserController userController;

        private User testUser;
        private User testAdmin;
        private UUID userId;
        private UUID adminId;

        @BeforeEach
        void setUp() {
                userId = UUID.randomUUID();
                adminId = UUID.randomUUID();

                testUser = new User();
                testUser.setId(userId);
                testUser.setEmail("user@test.com");
                testUser.setFirstName("Dodo");
                testUser.setLastName("Dada");
                testUser.setRole(User.Role.USER);
                testUser.setIsActivated(true);

                testAdmin = new User();
                testAdmin.setId(adminId);
                testAdmin.setEmail("admin@test.com");
                testAdmin.setFirstName("Admin");
                testAdmin.setLastName("User");
                testAdmin.setRole(User.Role.ADMIN);
                testAdmin.setIsActivated(true);
        }

        @Test
        void getCurrentUser_Success() {
                when(userService.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));

                ResponseEntity<AuthResponse> result = userController.getCurrentUser(testUser);

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertNotNull(result.getBody());
                assertEquals(testUser.getId(), result.getBody().getUserId());
                assertEquals(testUser.getEmail(), result.getBody().getEmail());
                assertEquals(testUser.getFirstName(), result.getBody().getFirstName());
                assertEquals(testUser.getLastName(), result.getBody().getLastName());
                assertEquals("USER", result.getBody().getRole());
                assertEquals(testUser.getIsActivated(), result.getBody().getIsActivated());
        }

        @Test
        void getCurrentUser_UserNotFound_ReturnsNotFound() {
                when(userService.findByEmail("user@test.com")).thenReturn(Optional.empty());

                ResponseEntity<AuthResponse> result = userController.getCurrentUser(testUser);

                assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
                assertNull(result.getBody());
        }

        @Test
        void updateCurrentUser_Success() {
                UserUpdateRequest request = new UserUpdateRequest();
                request.setFirstName("Lala");
                request.setLastName("Dada");

                User updatedUser = new User();
                updatedUser.setId(userId);
                updatedUser.setEmail("user@test.com");
                updatedUser.setFirstName("Lala");
                updatedUser.setLastName("Dada");

                when(userService.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
                when(userService.save(any(User.class))).thenReturn(updatedUser);

                ResponseEntity<User> result = userController.updateCurrentUser(testUser, request);

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertNotNull(result.getBody());
                assertEquals("Lala", result.getBody().getFirstName());
                assertEquals("Dada", result.getBody().getLastName());
                verify(userService).save(testUser);
        }

        @Test
        void updateCurrentUser_UserNotFound_ReturnsNotFound() {
                UserUpdateRequest request = new UserUpdateRequest();
                request.setFirstName("Lala");
                request.setLastName("Dada");

                when(userService.findByEmail("user@test.com")).thenReturn(Optional.empty());

                ResponseEntity<User> result = userController.updateCurrentUser(testUser, request);

                assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
                assertNull(result.getBody());
                verify(userService, never()).save(any());
        }

        @Test
        void deleteCurrentUser_Success() {
                when(userService.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));

                ResponseEntity<Void> result = userController.deleteCurrentUser(testUser);

                assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
                assertNull(result.getBody());
                verify(userService).deleteById(userId);
        }

        @Test
        void deleteCurrentUser_UserNotFound_ReturnsNotFound() {
                when(userService.findByEmail("user@test.com")).thenReturn(Optional.empty());

                ResponseEntity<Void> result = userController.deleteCurrentUser(testUser);

                assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
                verify(userService, never()).deleteById(any());
        }

        @Test
        void getAllUsers_Success() {
                List<User> users = Arrays.asList(testUser, testAdmin);
                when(userService.findAll()).thenReturn(users);

                ResponseEntity<List<User>> result = userController.getAllUsers();

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertNotNull(result.getBody());
                assertEquals(2, result.getBody().size());
                assertTrue(result.getBody().contains(testUser));
                assertTrue(result.getBody().contains(testAdmin));
        }

        @Test
        void getUserById_Success() {
                when(userService.findById(userId)).thenReturn(Optional.of(testUser));

                ResponseEntity<User> result = userController.getUserById(userId);

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertNotNull(result.getBody());
                assertEquals(testUser.getId(), result.getBody().getId());
                assertEquals(testUser.getEmail(), result.getBody().getEmail());
        }

        @Test
        void getUserById_UserNotFound_ReturnsNotFound() {
                when(userService.findById(userId)).thenReturn(Optional.empty());

                ResponseEntity<User> result = userController.getUserById(userId);

                assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
                assertNull(result.getBody());
        }

        @Test
        void updateUserById_Success() {
                User updateData = new User();
                updateData.setFirstName("Updated");
                updateData.setLastName("Name");
                updateData.setEmail("updated@test.com");
                updateData.setRole(User.Role.ADMIN);
                updateData.setIsActivated(false);

                User savedUser = new User();
                savedUser.setId(userId);
                savedUser.setFirstName("Updated");
                savedUser.setLastName("Name");
                savedUser.setEmail("updated@test.com");
                savedUser.setRole(User.Role.ADMIN);
                savedUser.setIsActivated(false);

                when(userService.findById(userId)).thenReturn(Optional.of(testUser));
                when(userService.save(testUser)).thenReturn(savedUser);

                ResponseEntity<User> result = userController.updateUserById(userId, updateData);

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertNotNull(result.getBody());
                assertEquals("Updated", result.getBody().getFirstName());
                assertEquals("Name", result.getBody().getLastName());
                assertEquals("updated@test.com", result.getBody().getEmail());
                assertEquals(User.Role.ADMIN, result.getBody().getRole());
                assertEquals(false, result.getBody().getIsActivated());
                verify(userService).save(testUser);
        }

        @Test
        void updateUserById_UserNotFound_ReturnsNotFound() {
                User updateData = new User();
                when(userService.findById(userId)).thenReturn(Optional.empty());

                ResponseEntity<User> result = userController.updateUserById(userId, updateData);

                assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
                assertNull(result.getBody());
                verify(userService, never()).save(any());
        }

        @Test
        void deleteUserById_Success() {
                when(userService.findById(userId)).thenReturn(Optional.of(testUser));

                ResponseEntity<User> result = userController.deleteUserById(userId);

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertNotNull(result.getBody());
                assertEquals(testUser.getId(), result.getBody().getId());
                verify(userService).deleteById(userId);
        }

        @Test
        void deleteUserById_UserNotFound_ReturnsNotFound() {
                when(userService.findById(userId)).thenReturn(Optional.empty());

                ResponseEntity<User> result = userController.deleteUserById(userId);

                assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
                assertNull(result.getBody());
                verify(userService, never()).deleteById(any());
        }

        @Test
        void activateUser_Success() {
                User savedUser = new User();
                savedUser.setId(userId);
                savedUser.setIsActivated(false);

                when(userService.findById(userId)).thenReturn(Optional.of(testUser));
                when(userService.save(testUser)).thenReturn(savedUser);

                ResponseEntity<User> result = userController.activateUser(userId, false);

                assertEquals(HttpStatus.OK, result.getStatusCode());
                assertNotNull(result.getBody());
                assertEquals(false, result.getBody().getIsActivated());
                verify(userService).save(testUser);
        }

        @Test
        void activateUser_UserNotFound_ReturnsNotFound() {
                when(userService.findById(userId)).thenReturn(Optional.empty());

                ResponseEntity<User> result = userController.activateUser(userId, false);

                assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
                assertNull(result.getBody());
                verify(userService, never()).save(any());
        }
}