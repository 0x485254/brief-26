package com.easygroup.controller;

import com.easygroup.dto.AuthResponse;
import com.easygroup.entity.User;
import com.easygroup.security.IsAdmin;
import com.easygroup.security.IsAuthenticated;
import com.easygroup.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for user management endpoints.
 * Demonstrates the use of guard decorators.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get the current user's profile.
     * This endpoint is protected by the IsAuthenticated guard.
     *
     * @return the current user
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        String email = user.getEmail();
        Optional<User> userFound = userService.findByEmail(email);
        
        if (userFound.isPresent()) {
            AuthResponse userResponse = new AuthResponse();
            userResponse.setUserId(userFound.get().getId());
            userResponse.setEmail(userFound.get().getEmail());
            userResponse.setFirstName(userFound.get().getFirstName());
            userResponse.setLastName(userFound.get().getLastName());
            userResponse.setRole(String.valueOf(userFound.get().getRole()));
            userResponse.setIsActivated(userFound.get().getIsActivated());

            return ResponseEntity.ok(userResponse);
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Get all users.
     * This endpoint is protected by the IsAdmin guard.
     *
     * @return list of all users
     */
    @GetMapping
    @IsAdmin
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Get a user by ID.
     * This endpoint is protected by the IsAdmin guard.
     *
     * @param id the user ID
     * @return the user
     */
    @GetMapping("/{id}")
    // @IsAdmin
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update a user by ID.
     * This endpoint is protected by the IsAdmin guard.
     *
     * @param id the user ID
     * @return the user
     */
    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<User> updateUserById(@PathVariable UUID id, @RequestBody @Valid User updatedUser) {
        return userService.findById(id)
                .map(user -> {
                    // Update allowed fields
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    user.setEmail(updatedUser.getEmail());
                    user.setRole(updatedUser.getRole());
                    user.setIsActivated(updatedUser.getIsActivated());
                
                    // Save and return updated user
                    return ResponseEntity.ok(userService.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
}

    /**
     * Delete a user by ID.
     * This endpoint is protected by the IsAdmin guard.
     *
     * @param id the user ID
     * @return the user
     */
    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<User> deleteUserById(@PathVariable UUID id) {
        return userService.findById(id)
                .map(user -> {
                    userService.deleteById(id);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
}

    /**
     * Activate or deactivate a user.
     * This endpoint is protected by the IsAdmin guard.
     *
     * @param id the user ID
     * @param isActivated whether the user should be activated
     * @return the updated user
     */
@PutMapping("/{id}/activate")
@IsAdmin
public ResponseEntity<User> activateUser(@PathVariable UUID id, @RequestParam boolean isActivated) {
        return userService.findById(id)
                .map(user -> {
    user.setIsActivated(isActivated);
    return ResponseEntity.ok(userService.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
}

    /**
     * Update the current user's profile.
     * This endpoint is protected by the IsAuthenticated guard.
     *
     * @param updatedUser the updated user data
     * @return the updated user
     */
    @PutMapping("/me")
    @IsAuthenticated
    public ResponseEntity<User> updateCurrentUser(@RequestBody @Valid User updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userService.findByEmail(email)
                .map(user -> {
                    // Only update allowed fields (prevent changing role, etc.)
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    // Email update would require re-authentication, so it's not included here

                    return ResponseEntity.ok(userService.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete the current user's account.
     * This endpoint is protected by the IsAuthenticated guard.
     *
     * @return a success response with no content
     */
    @DeleteMapping("/me")
    @IsAuthenticated
    public ResponseEntity<Void> deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userService.findByEmail(email)
                .map(user -> {
                    userService.deleteById(user.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}