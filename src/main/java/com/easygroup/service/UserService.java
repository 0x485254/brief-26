package com.easygroup.service;

import com.easygroup.entity.User;
import com.easygroup.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing users.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find all users.
     *
     * @return a list of all users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Find a user by ID.
     *
     * @param id the user ID
     * @return an Optional containing the user if found
     */
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Find a user by email.
     *
     * @param email the user email
     * @return an Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Save a user.
     *
     * @param user the user to save
     * @return the saved user
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Delete a user by ID.
     *
     * @param id the user ID
     */
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    /**
     * Check if a user exists with the given email.
     *
     * @param email the email to check
     * @return true if a user exists with the email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getActivatedUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(User::getIsActivated)
                .orElseThrow(() -> new UsernameNotFoundException("User not found or not activated: " + email));
    }

}
