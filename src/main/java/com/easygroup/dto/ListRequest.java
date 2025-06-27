package com.easygroup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new list.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListRequest {

    @NotBlank(message = "List name is required")
    @Size(min = 3, max = 255, message = "List name must be between 3 and 255 characters")
    private String name;
}