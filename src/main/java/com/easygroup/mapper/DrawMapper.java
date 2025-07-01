package com.easygroup.mapper;

import com.easygroup.dto.DrawResponse;
import com.easygroup.entity.Draw;
import java.util.stream.Collectors;

public class DrawMapper {

    public static DrawResponse toDto(Draw draw) {
        return DrawResponse.builder()
                .id(draw.getId())
                .title(draw.getTitle())
                .createdAt(draw.getCreatedAt())
                .listId(draw.getList().getId())
                .listName(draw.getList().getName())
                .groupCount(draw.getGroups() != null ? draw.getGroups().size() : 0)
                .groups(draw.getGroups().stream()
                        .map(GroupMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }
}