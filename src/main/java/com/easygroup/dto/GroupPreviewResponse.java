package com.easygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupPreviewResponse {
    private UUID listId;
    private String listName;
    private String title;
    private List<GroupResponse> groups;
    private Integer groupCount;
    private Integer totalPersons;
    private LocalDateTime generatedAt;

}