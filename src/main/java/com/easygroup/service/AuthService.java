package com.easygroup.service;

import com.easygroup.dto.AuthResponse;
import com.easygroup.entity.User;
import com.easygroup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
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
        user.setPassword(passwordEncoder.encode(password)); // Password is now properly encrypted
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setIsActivated(true);
        user.setCguDate(LocalDate.now());
        user.setRole(User.Role.USER);

        return userRepository.save(user);
    }

    /**
     * Authenticate a user and generate a JWT token.
     *
     * @param email the user's email
     * @param password the user's password
     * @return an AuthResponse containing the user details and JWT token
     * @throws AuthenticationException if authentication fails
     */
    public AuthResponse authenticate(String email, String password) {
        // Use Spring Security's AuthenticationManager to authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // If we get here, authentication was successful
        var user = userDetailsService.getUserByEmail(email);
        var jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(email));

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                jwtToken,
                user.getRole().toString()
        );
    }

    /**
     * Change a user's password.
     *
     * @param user the user
     * @param newPassword the new password
     * @return the updated user
     */
    public User changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword)); // Password is now properly encrypted
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
