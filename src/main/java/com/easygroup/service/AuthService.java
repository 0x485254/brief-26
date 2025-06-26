package com.easygroup.service;

import com.easygroup.entity.User;
import com.easygroup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for authentication and user registration.
 */
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Register a new user.
     *
     * @param email the user's email
     * @param password the user's password
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @return the created user
     * @throws IllegalArgumentException if a user with the email already exists
     */
    public User register(String email, String password, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password); // In a real application, this would be encrypted
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setIsActivated(true);
        user.setCguDate(LocalDate.now());
        user.setRole(User.Role.USER);

        return userRepository.save(user);
    }

    /**
     * Authenticate a user.
     *
     * @param email the user's email
     * @param password the user's password
     * @return an Optional containing the user if authentication is successful
     */
    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        // In a real application, password would be encrypted and compared securely
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt;
        }
        
        return Optional.empty();
    }

    /**
     * Change a user's password.
     *
     * @param user the user
     * @param newPassword the new password
     * @return the updated user
     */
    public User changePassword(User user, String newPassword) {
        // In a real application, password would be encrypted
        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Activate or deactivate a user.
     *
     * @param user the user
     * @param isActivated whether the user should be activated
     * @return the updated user
     */
    public User setActivated(User user, boolean isActivated) {
        user.setIsActivated(isActivated);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}