package com.easygroup.service;

import com.easygroup.dto.AuthResponse;
import com.easygroup.entity.User;
import com.easygroup.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final CookieService cookieService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationConfiguration authenticationConfiguration, // 👈 Injection du config
            UserService userService,
            CookieService cookieService) throws Exception {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        authenticationConfiguration.getAuthenticationManager();
        this.userService = userService;
        this.cookieService = cookieService;
    }

    public User register(String email, String password, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
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

    public AuthResponse authenticate(String email, String password, HttpServletResponse response) {
        User user = userService.getActivatedUserByEmail(email);
        String token = jwtService.generateToken(user);

        cookieService.addTokenCookie(response, token);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                null,
                user.getRole().toString(),
                user.getIsActivated());
    }

    public void logout(HttpServletResponse response) {
        cookieService.clearTokenCookie(response);
    }

    public User changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User setActivated(User user, boolean isActivated) {
        user.setIsActivated(isActivated);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
