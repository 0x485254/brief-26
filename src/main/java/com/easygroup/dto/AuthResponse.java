package com.easygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
    private String token;
    private String role;
}