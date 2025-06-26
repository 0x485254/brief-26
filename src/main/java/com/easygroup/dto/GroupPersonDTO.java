package com.easygroup.dto;

import com.easygroup.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for group-person associations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPersonDTO {

    private Integer groupId;
    private String groupName;
    private Integer personId;
    private String personName;
    private Person.Gender gender;
    private Integer age;
    private Integer frenchLevel;
    private Boolean oldDwwm;
    private Integer techLevel;
    private Person.Profile profile;
}