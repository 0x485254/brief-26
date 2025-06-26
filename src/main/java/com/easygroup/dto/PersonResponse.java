package com.easygroup.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {

    private UUID personId;
    private String name;
    private String gender;
    private Integer age;
    private Integer frenchLevel;
    private Boolean oldDwwm;
    private Integer techLevel;
    private String profile;

}
