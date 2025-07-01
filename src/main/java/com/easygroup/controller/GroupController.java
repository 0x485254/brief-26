package com.easygroup.controller;

import com.easygroup.dto.GroupResponse;
import com.easygroup.entity.Group;
import com.easygroup.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class GroupController {

    @Autowired
    private GroupService groupService;

    /**
     * GET /api/draws/{drawId}/groups
     * Get all groups generated for a specific draw
     */
    @GetMapping("/draws/{drawId}/groups")
    public ResponseEntity<List<GroupResponse>> getGroupsForDraw(
            @PathVariable UUID drawId) {

        List<GroupResponse> groups = groupService.getGroupsByDrawId(drawId);
        return ResponseEntity.ok(groups);
    }

    /**
     * POST /api/groups/{groupId}/persons/{personId}
     * Add a person to an existing group (manual adjustment)
     */
    @PostMapping("/groups/{groupId}/persons/{personId}")
    public ResponseEntity<Void> addPersonToGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID personId) {

        groupService.addPersonToGroup(groupId, personId);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/groups/{groupId}/persons/{personId}
     * Remove a person from a group (manual adjustment)
     */
    @DeleteMapping("/groups/{groupId}/persons/{personId}")
    public ResponseEntity<Void> removePersonFromGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID personId) {

        groupService.removePersonFromGroup(groupId, personId);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/draws/{drawId}/groups
     * Create a new group linked to a draw
     */
    @PostMapping("/draws/{drawId}/groups")
    public ResponseEntity<Void> createGroup(
            @PathVariable UUID drawId,
            @RequestBody Group group) {
        groupService.createGroup(group, drawId);
        return ResponseEntity.status(201).build();
    }

    /**
     * PUT /api/groups/{groupId}
     * Update an existing group
     */
    @PutMapping("/groups/{groupId}")
    public ResponseEntity<Group> updateGroup(
            @PathVariable UUID groupId,
            @RequestBody Group group) {
        Group updated = groupService.updateGroup(groupId, group);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/groups/{groupId}
     * Delete a group
     */
    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID groupId) {
        boolean deleted = groupService.deleteGroup(groupId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}