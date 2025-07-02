package com.easygroup.controller;

import com.easygroup.dto.AuthResponse;
import com.easygroup.dto.LoginRequest;
import com.easygroup.dto.RegisterRequest;
import com.easygroup.service.AuthService;

import com.easygroup.service.MailingService;
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

/**
 * Controller for cookie-based authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Value("${smtp.server}")
    private String smtpServer;

    @Value("${smtp.username}")
    private String smtpUsername;

    @Value("${smtp.password}")
    private String smtpPassword;



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
            authService.register(request.getEmail(), request.getPassword(), request.getFirstName(), request.getLastName());

            sendValidationEmail(request.getEmail(), request.getFirstName());

            return ResponseEntity.status(HttpStatus.CREATED).body(true);
        } catch (IllegalArgumentException | MessagingException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    private void sendValidationEmail(String userEmail, String userName) throws MessagingException {
        MailingService mailingService = new MailingService(
                smtpServer,
                smtpUsername,
                smtpPassword
        );

        // Send an HTML email
        try {
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
                            "            <a href=\"https://yourwebsite.com/verify?token={token}\" class=\"button\">Verify My Email</a>\n" +
                            "        </div>\n" +
                            "        \n" +
                            "        <p>If the button above doesn't work, copy and paste the following URL into your browser:</p>\n" +
                            "        <p style=\"word-break: break-all; font-size: 12px;\">https://yourwebsite.com/verify?token={token}</p>\n" +
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
