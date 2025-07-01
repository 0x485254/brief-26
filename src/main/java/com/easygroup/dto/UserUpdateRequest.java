package com.easygroup.dto;

import jakarta.validation.constraints.NotBlank;

public class UserUpdateRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    // Getters & Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
