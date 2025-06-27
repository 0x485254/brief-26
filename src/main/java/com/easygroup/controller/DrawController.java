package com.easygroup.controller;

import com.easygroup.dto.DrawResponse;
import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.service.DrawService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DrawController {

    @Autowired
    private DrawService drawService;

    /**
     * POST /api/users/{userId}/lists/{listId}/draws
     * Create a new draw (tirage) and generate groups
     */
    @PostMapping("/users/{userId}/lists/{listId}/draws")
    public ResponseEntity<DrawResponse> createDraw(
            @PathVariable UUID userId,
            @PathVariable UUID listId,
            @Valid @RequestBody GenerateGroupsRequest request) {

        DrawResponse response = drawService.generateGroups(request, userId, listId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/users/{userId}/lists/{listId}/draws
     * List all draws for a specific list
     */
    @GetMapping("/users/{userId}/lists/{listId}/draws")
    public ResponseEntity<List<DrawResponse>> getDrawsForList(
            @PathVariable UUID userId,
            @PathVariable UUID listId) {

        List<DrawResponse> draws = drawService.getDrawsForList(userId, listId);
        return ResponseEntity.ok(draws);
    }
}