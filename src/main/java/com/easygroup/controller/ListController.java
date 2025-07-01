package com.easygroup.controller;

import com.easygroup.dto.ListRequest;
import com.easygroup.dto.ListResponse;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.User;
import com.easygroup.service.ListService;
import com.easygroup.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/lists")
public class ListController {

    private final ListService listService;
    private final UserService userService;

    public ListController(ListService listService, UserService userService) {
        this.listService = listService;
        this.userService = userService;
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

    // Get a list by its id
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

    // Get all lists of a specific user by ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<java.util.List<ListResponse>> getListsByUserId(@PathVariable UUID userId) {
        User foundUser = userService.findById(userId).orElse(null);
        if (foundUser == null) {
            return ResponseEntity.notFound().build();
        }

        java.util.List<ListResponse> responseList = listService.findByUser(foundUser).stream()
                .map(list -> new ListResponse(
                        list.getId(),
                        list.getName(),
                        list.getIsShared()))
                .toList();

        return ResponseEntity.ok(responseList);
    }

}
