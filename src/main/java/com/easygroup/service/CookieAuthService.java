package com.easygroup.service;

import com.easygroup.dto.AuthResponse;
import com.easygroup.entity.User;
import com.easygroup.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service for cookie-based authentication and user registration.
 */
@Service
@Transactional
public class CookieAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final CookieService cookieService;

    public CookieAuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            CookieService cookieService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.cookieService = cookieService;
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

        // Validate that firstName and lastName are provided
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setIsActivated(true);
        user.setCguDate(LocalDate.now());
        user.setRole(User.Role.USER);

        return userRepository.save(user);
    }

    /**
     * Authenticate a user and set a JWT token cookie.
     *
     * @param email the user's email
     * @param password the user's password
     * @param response the HTTP response to set the cookie on
     * @return an AuthResponse containing the user details (without the token)
     * @throws AuthenticationException if authentication fails
     */
    public AuthResponse authenticate(String email, String password, HttpServletResponse response) {
        // Use Spring Security's AuthenticationManager to authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // If we get here, authentication was successful
        var user = userDetailsService.getUserByEmail(email);
        var jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(email));

        // Set the JWT token as a cookie
        cookieService.addTokenCookie(response, jwtToken);

        // Return user details without the token
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUserId(user.getId());
        authResponse.setEmail(user.getEmail());
        authResponse.setFirstName(user.getFirstName());
        authResponse.setLastName(user.getLastName());
        authResponse.setToken(null); // No token in response body
        authResponse.setRole(user.getRole().toString());
        authResponse.setIsActivated(user.getIsActivated());

        return authResponse;
    }

    /**
     * Logout a user by clearing the JWT token cookie.
     *
     * @param response the HTTP response to clear the cookie from
     */
    public void logout(HttpServletResponse response) {
        cookieService.clearTokenCookie(response);
    }

    /**
     * Change a user's password.
     *
     * @param user the user
     * @param newPassword the new password
     * @return the updated user
     */
    public User changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
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
