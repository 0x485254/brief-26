package com.easygroup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListResponse {
    private UUID id;
    private String name;
    private Boolean isShared;
}
