package com.easygroup.controller;

import com.easygroup.dto.ShareListRequest;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.ListShare;
import com.easygroup.entity.User;
import com.easygroup.service.ListService;
import com.easygroup.service.ListShareService;
import com.easygroup.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lists/{listId}/share")
public class ListShareController {

    private final ListShareService listShareService;
    private final ListService listService;
    private final UserService userService;

    public ListShareController(ListShareService listShareService, ListService listService, UserService userService) {
        this.listShareService = listShareService;
        this.listService = listService;
        this.userService = userService;
    }

    // share a list to a user
    @PostMapping
    public ResponseEntity<ListShare> shareList(
            @AuthenticationPrincipal User owner,
            @RequestBody ShareListRequest request,
            @PathVariable UUID listId) {

        ListEntity list = listService.findByIdAndUserId(listId, owner.getId());
        if (list == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User userSharedTo = null;
        if (request.userId() != null) {
            userSharedTo = userService.findById(request.userId()).orElse(null);
        } else if (request.email() != null) {
            userSharedTo = userService.findByEmail(request.email()).orElse(null);
        }

        if (userSharedTo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ListShare listShared = listShareService.shareList(list, userSharedTo);
        return ResponseEntity.ok(listShared);
    }

    // Unshare a list to a user
    @DeleteMapping("/{sharedUserId}")
    public ResponseEntity<ListEntity> deleteListShare(@AuthenticationPrincipal User owner,
            @RequestBody User userUnshareTo, @PathVariable UUID listId) {
        ListEntity list = this.listService.findById(listId).orElseThrow();

        if (owner.getLists().stream().anyMatch(l -> l.equals(list))) {
            ListEntity listUnshared = this.listShareService.unshareList(list, userUnshareTo);

            return ResponseEntity.ok(listUnshared);
        }
        return ResponseEntity.notFound().build();

    }

}
