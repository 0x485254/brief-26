package com.easygroup.mapper;

import com.easygroup.dto.GroupResponse;
import com.easygroup.entity.Group;
import java.util.stream.Collectors;

public class GroupMapper {

    public static GroupResponse toDto(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .drawId(group.getDraw() != null ? group.getDraw().getId() : null)
                .persons(group.getPersons().stream()
                        .map(PersonMapper::toDto)
                        .collect(Collectors.toList()))
                .personCount(group.getPersons().size())
                .build();
    }

    public static GroupResponse toDtoPreview(Group group) {
        return GroupResponse.builder()
                .id(null) // Preview mode
                .name(group.getName())
                .drawId(null) // Preview mode
                .persons(group.getPersons().stream()
                        .map(PersonMapper::toDto)
                        .collect(Collectors.toList()))
                .personCount(group.getPersons().size())
                .build();
    }
}