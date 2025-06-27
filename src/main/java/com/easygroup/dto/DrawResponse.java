package com.easygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrawResponse {

    private UUID id;
    private String title;
    private LocalDateTime createdAt;
    private UUID listId;
    private String listName;
    private Integer groupCount;
}