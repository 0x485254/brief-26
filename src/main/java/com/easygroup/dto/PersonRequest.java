package com.easygroup.dto;
import com.easygroup.entity.Person;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Gender is required")
    private Person.Gender gender;
    
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be positive")
    @Max(value = 150, message = "Age must be realistic")
    private Integer age;
    
    @NotNull(message = "French level is required")
    @Min(value = 1, message = "French level must be between 1 and 5")
    @Max(value = 5, message = "French level must be between 1 and 5")
    private Integer frenchLevel;
    
    @NotNull(message = "Old Dwwm is required")
    private Boolean oldDwwm;

    @NotNull(message = "Tech level is required")
    @Min(value = 1, message = "Tech level must be between 1 and 5")
    @Max(value = 5, message = "Tech level must be between 1 and 5")
    private Integer techLevel;

    @NotNull(message = "Profile is required")
    private Person.Profile profile;
}