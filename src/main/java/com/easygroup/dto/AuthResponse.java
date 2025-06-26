package com.easygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for authentication responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private String token;
    private String role;
}
