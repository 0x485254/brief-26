package com.easygroup.controller;

import com.easygroup.dto.AuthResponse;
import com.easygroup.dto.LoginRequest;
import com.easygroup.dto.RegisterRequest;
import com.easygroup.entity.User;
import com.easygroup.service.AuthService;
import com.easygroup.service.JwtService;
import com.easygroup.service.MailingService;
import com.easygroup.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for cookie-based authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService;
    private final MailingService mailingService;

    public AuthController(AuthService authService, JwtService jwtService, UserService userService,
            MailingService mailingService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.mailingService = mailingService;
    }

    @Value("${smtp.server}")
    private String smtpServer;

    @Value("${smtp.username}")
    private String smtpUsername;

    @Value("${smtp.password}")
    private String smtpPassword;

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${mail.from}")
    private String mailFrom;

    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Crée un nouveau compte utilisateur à partir d'un email, mot de passe, prénom et nom, avec vérification par mail")
    @PostMapping("/register-mail")
    public ResponseEntity<Boolean> registerWithMail(
            @RequestBody @Valid RegisterRequest request,
            HttpServletResponse response) {
        try {
            User userResponse = authService.register(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName());

            String token = jwtService.generateValidationToken(userResponse);

            if (StringUtils.hasText(smtpServer) &&
                    StringUtils.hasText(smtpUsername) &&
                    StringUtils.hasText(smtpPassword) &&
                    StringUtils.hasText(applicationUrl)) {
                try {
                    sendValidationEmail(request.getEmail(), request.getFirstName(), token);
                } catch (MessagingException e) {
                    // Log error, but don't interrupt flow
                    System.err.println("❌ Email not sent: " + e.getMessage());
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(true);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Crée un nouveau compte utilisateur activé immédiatement (sans vérification e-mail)")
    @PostMapping("/register")
    public ResponseEntity<Boolean> register(
            @RequestBody @Valid RegisterRequest request,
            HttpServletResponse response) {
        try {
            User user = authService.register(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName());

            user.setIsActivated(true); // ✅ Activation immédiate

            userService.save(user); // ✅ Persistance de la modification

            return ResponseEntity.status(HttpStatus.CREATED).body(true);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    private void sendValidationEmail(String userEmail, String userName, String token) throws MessagingException {
        String url = applicationUrl + "/api/auth/verify?token=" + token;

        String htmlContent = "<html>\n" +
                "<head>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333333; max-width: 600px; margin: 0 auto; padding: 20px; }\n"
                +
                "        .header { text-align: center; padding-bottom: 20px; border-bottom: 1px solid #eeeeee; }\n" +
                "        .content { padding: 20px 0; }\n" +
                "        .button { display: inline-block; background-color: #4CAF50; color: white; text-decoration: none; padding: 12px 24px; border-radius: 4px; font-weight: bold; margin: 20px 0; }\n"
                +
                "        .footer { font-size: 12px; color: #777777; text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eeeeee; }\n"
                +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"header\">\n" +
                "        <h1>Email Verification</h1>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "        <p>Hello, " + userName + "</p>\n" +
                "        <p>Thank you for registering with our service. To complete your registration and verify your email address, please click the button below:</p>\n"
                +
                "        <div style=\"text-align: center;\">\n" +
                "            <a href=\"" + url + "\" class=\"button\">Verify My Email</a>\n" +
                "        </div>\n" +
                "        <p>If the button above doesn't work, copy and paste the following URL into your browser:</p>\n"
                +
                "        <p style=\"word-break: break-all; font-size: 12px;\">" + url + "</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        mailingService.sendHtmlEmail(mailFrom, userEmail, "Validation", htmlContent);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            authService.verifyAccount(token);
            String redirectUrl = "https://brief-react-v3-groupshuffle-11e877.gitlab.io/#/login";

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", applicationUrl + "/verification-failed")
                    .build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response) {

        Optional<User> user = userService.findByEmail(request.getEmail());

        if (user.isEmpty() || !user.get().getIsActivated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            AuthResponse authResponse = authService.authenticate(request.getEmail(), request.getPassword(), response);
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Créer un administrateur (réservé aux admins)")
    @PostMapping("/register-admin")
    public ResponseEntity<User> registerAdmin(@RequestBody @Valid RegisterRequest request) {
        try {
            User user = authService.register(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName());
            user.setRole(User.Role.ADMIN);
            user.setIsActivated(true);
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
