package com.easygroup.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateGroupsRequest {

    private String title;

    @NotNull(message = "Number of groups is required")
    @Min(value = 2, message = "At least 2 groups are required")
    private Integer numberOfGroups;

    @NotEmpty(message = "Group names are required")
    private List<String> groupNames;

    private Boolean balanceByGender = false;
    private Boolean balanceByAge = false;
    private Boolean balanceByFrenchLevel = false;
    private Boolean balanceByTechLevel = false;
    private Boolean balanceByOldDwwm = false;
    private Boolean balanceByProfile = false;
}