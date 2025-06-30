package com.easygroup.mapper;

import com.easygroup.dto.PersonResponse;
import com.easygroup.entity.Person;

public class PersonMapper {

    public static PersonResponse toDto(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getName(),
                person.getGender().name(),
                person.getAge(),
                person.getFrenchLevel(),
                person.getOldDwwm(),
                person.getTechLevel(),
                person.getProfile().name()
        );
    }
}
