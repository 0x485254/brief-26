package com.easygroup.controller;

import com.easygroup.dto.AuthResponse;
import com.easygroup.entity.User;
import com.easygroup.security.IsAdmin;
import com.easygroup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ===========================
    // Routes for current user (/me)
    // ===========================

    /**
     * Get the current user's profile.
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        Optional<User> userFound = userService.findByEmail(user.getEmail());

        return userFound.map(u -> {
            AuthResponse res = new AuthResponse();
            res.setUserId(u.getId());
            res.setEmail(u.getEmail());
            res.setFirstName(u.getFirstName());
            res.setLastName(u.getLastName());
            res.setRole(String.valueOf(u.getRole()));
            res.setIsActivated(u.getIsActivated());
            return ResponseEntity.ok(res);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update the current user's profile.
     */
    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(@AuthenticationPrincipal User user, @RequestBody @Valid User updatedUser) {
        return userService.findByEmail(user.getEmail())
                .map(existing -> {
                    existing.setFirstName(updatedUser.getFirstName());
                    existing.setLastName(updatedUser.getLastName());
                    return ResponseEntity.ok(userService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete the current user's account.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal User user) {
        return userService.findByEmail(user.getEmail())
                .map(u -> {
                    userService.deleteById(u.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ===========================
    // Admin routes (require @IsAdmin)
    // ===========================

    /**
     * Get all users.
     */
    @GetMapping
    @IsAdmin
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Get a user by ID.
     */
    @GetMapping("/{id}")
    @IsAdmin
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update a user by ID.
     */
    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<User> updateUserById(@PathVariable UUID id, @RequestBody @Valid User updatedUser) {
        return userService.findById(id)
                .map(user -> {
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    user.setEmail(updatedUser.getEmail());
                    user.setRole(updatedUser.getRole());
                    user.setIsActivated(updatedUser.getIsActivated());
                    return ResponseEntity.ok(userService.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a user by ID.
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
}
