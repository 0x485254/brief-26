package com.easygroup.controller;

import com.easygroup.dto.DrawResponse;
import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.dto.GroupPreviewResponse;
import com.easygroup.entity.User;
import com.easygroup.service.DrawService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DrawController {

    @Autowired
    private DrawService drawService;

    /**
     * POST /api/lists/{listId}/draws?save=false (PREVIEW)
     * POST /api/lists/{listId}/draws?save=true (SAVE)
     * Generate groups with optional save
     */
    @PostMapping("/lists/{listId}/draws/preview")
    public ResponseEntity<GroupPreviewResponse> generatePreview(
            @AuthenticationPrincipal User user,
            @PathVariable UUID listId,
            @Valid @RequestBody GenerateGroupsRequest request) {

        GroupPreviewResponse response = drawService.generatePreview(request, user.getId(), listId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lists/{listId}/draws")
    public ResponseEntity<DrawResponse> saveGroups(
            @AuthenticationPrincipal User user,
            @PathVariable UUID listId,
            @Valid @RequestBody GroupPreviewResponse request) { // Frontend sends back the modified preview!

        DrawResponse response = drawService.saveModifiedGroups(request, user.getId(), listId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/lists/{listId}/draws
     * List all draws for a specific list
     */
    @GetMapping("/lists/{listId}/draws")
    public ResponseEntity<List<DrawResponse>> getDrawsForList(
            @AuthenticationPrincipal User user,
            @PathVariable UUID listId) {

        List<DrawResponse> draws = drawService.getDrawsForList(user.getId(), listId);
        return ResponseEntity.ok(draws);
    }
}