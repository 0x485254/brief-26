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
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
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

    public AuthController(AuthService authService, JwtService jwtService, UserService userService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Value("${smtp.server}")
    private String smtpServer;

    @Value("${smtp.username}")
    private String smtpUsername;

    @Value("${smtp.password}")
    private String smtpPassword;

    @Value("${application.url}")
    private String applicationUrl;



    /**
     * Register a new user and set a JWT token cookie.
     *
     * @param request the registration request
     * @param response the HTTP response to set the cookie on
     * @return the created user details (without the token in the response body)
     */
    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Crée un nouveau compte utilisateur à partir d'un email, mot de passe, prénom et nom")
    @PostMapping("/register")
    public ResponseEntity<Boolean> register(
            @RequestBody @Valid RegisterRequest request,
            HttpServletResponse response) {
        try {
            // Register the user
            User userResponse = authService.register(request.getEmail(), request.getPassword(), request.getFirstName(), request.getLastName());

            String token = jwtService.generateValidationToken(userResponse);

            sendValidationEmail(request.getEmail(), request.getFirstName(), token);

            return ResponseEntity.status(HttpStatus.CREATED).body(true);
        } catch (IllegalArgumentException | MessagingException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Sends a validation email to the specified user with an HTML email format.
     * The email contains a verification link for the user to confirm their email address.
     *
     * @param userEmail the email address of the recipient
     * @param userName the name of the recipient to personalize the email
     * @throws MessagingException if there is an issue while sending the email
     */
    private void sendValidationEmail(String userEmail, String userName, String token) throws MessagingException {
        MailingService mailingService = new MailingService(
                smtpServer,
                smtpUsername,
                smtpPassword
        );

        // Send an HTML email
        try {
            String url = applicationUrl + "/api/auth/verify?token="  + token;

            mailingService.sendHtmlEmail(
                    smtpUsername,
                    userEmail ,
                    "Validation",
                    "<html>\n" +
                            "<head>\n" +
                            "    <style>\n" +
                            "        body {\n" +
                            "            font-family: Arial, sans-serif;\n" +
                            "            line-height: 1.6;\n" +
                            "            color: #333333;\n" +
                            "            max-width: 600px;\n" +
                            "            margin: 0 auto;\n" +
                            "            padding: 20px;\n" +
                            "        }\n" +
                            "        .header {\n" +
                            "            text-align: center;\n" +
                            "            padding-bottom: 20px;\n" +
                            "            border-bottom: 1px solid #eeeeee;\n" +
                            "        }\n" +
                            "        .content {\n" +
                            "            padding: 20px 0;\n" +
                            "        }\n" +
                            "        .button {\n" +
                            "            display: inline-block;\n" +
                            "            background-color: #4CAF50;\n" +
                            "            color: white;\n" +
                            "            text-decoration: none;\n" +
                            "            padding: 12px 24px;\n" +
                            "            border-radius: 4px;\n" +
                            "            font-weight: bold;\n" +
                            "            margin: 20px 0;\n" +
                            "        }\n" +
                            "        .footer {\n" +
                            "            font-size: 12px;\n" +
                            "            color: #777777;\n" +
                            "            text-align: center;\n" +
                            "            margin-top: 30px;\n" +
                            "            padding-top: 20px;\n" +
                            "            border-top: 1px solid #eeeeee;\n" +
                            "        }\n" +
                            "    </style>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "    <div class=\"header\">\n" +
                            "        <h1>Email Verification</h1>\n" +
                            "    </div>\n" +
                            "    <div class=\"content\">\n" +
                            "        <p>Hello," + userName + "</p>\n" +
                            "        <p>Thank you for registering with our service. To complete your registration and verify your email address, please click the button below:</p>\n" +
                            "        \n" +
                            "        <div style=\"text-align: center;\">\n" +
                            "            <a href=" + url + " class=\"button\">Verify My Email</a>\n" +
                            "        </div>\n" +
                            "        \n" +
                            "        <p>If the button above doesn't work, copy and paste the following URL into your browser:</p>\n" +
                            "        <p style=\"word-break: break-all; font-size: 12px;\">" + url + "</p>\n" +
                            "    </div>\n" +
                            "</body>\n" +
                            "</html>"
            );
            System.out.println("HTML email sent successfully!");
        } catch (MessagingException e) {
            System.err.println("Failed to send HTML email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verifies a user's email by validating the provided token.
     * If the token is valid, the user's account is activated.
     *
     * @param token the token used for email verification
     * @return a ResponseEntity containing a boolean value indicating whether the operation was successful
     */
    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifyEmail(@RequestParam("token") String token) {
        authService.verifyAccount(token);
        return ResponseEntity.ok(true);
    }

    /**
     * Authenticate a user and set a JWT token cookie.
     *
     * @param request the authentication request
     * @param response the HTTP response to set the cookie on
     * @return the authenticated user details (without the token in the response body)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response) {

        Optional<User> user = userService.findByEmail(request.getEmail());

        System.out.println("-----------------ACTIVATED-------------------");
        System.out.println(user.get().getIsActivated());

        if (user.isEmpty() || user.get().getIsActivated() ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            AuthResponse authResponse = authService.authenticate(request.getEmail(), request.getPassword(), response);
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Logout a user by clearing the JWT token cookie.
     *
     * @param response the HTTP response to clear the cookie from
     * @return a success response
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok().build();
    }
}
