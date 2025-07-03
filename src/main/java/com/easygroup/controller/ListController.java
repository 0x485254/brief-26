package com.easygroup.controller;

import com.easygroup.dto.ListRequest;
import com.easygroup.dto.ListResponse;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.User;
import com.easygroup.service.ListService;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lists")
public class ListController {

    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    // Create a list for a user
    @PostMapping
    public ResponseEntity<ListResponse> createList(@RequestBody ListRequest request,
            @AuthenticationPrincipal User user) {
        System.out.println("USER = " + user);

        try {
            ListEntity list = listService.save(request.getName(), user);

            ListResponse response = new ListResponse(
                    list.getId(),
                    list.getName(),
                    list.getIsShared());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all lists of a specific user
    @GetMapping
    public ResponseEntity<java.util.List<ListResponse>> getListByUser(@AuthenticationPrincipal User user) {
        try {
            java.util.List<ListResponse> responseList = listService.findByUser(user).stream()
                    .map(list -> new ListResponse(
                            list.getId(),
                            list.getName(),
                            list.getIsShared()))
                    .toList();

            return ResponseEntity.ok(responseList);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get a specific list by ID, only if owned by the authenticated user
    @GetMapping("/{listId}")
    public ResponseEntity<ListResponse> getListById(
            @PathVariable UUID listId,
            @AuthenticationPrincipal User user) {

        ListEntity list = listService.findByIdAndUserId(listId, user.getId());
        if (list == null) {
            return ResponseEntity.status(403).build(); // liste non trouvée ou n'appartient pas à l'utilisateur
        }

        ListResponse response = new ListResponse(
                list.getId(),
                list.getName(),
                list.getIsShared());

        return ResponseEntity.ok(response);
    }

}
