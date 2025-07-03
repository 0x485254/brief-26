package com.easygroup.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easygroup.dto.ListResponse;
import com.easygroup.entity.User;
import com.easygroup.security.IsAdmin;
import com.easygroup.service.ListService;
import com.easygroup.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ListService listService;
    private final UserService userService;

    public AdminController(ListService listService, UserService userService) {
        this.listService = listService;
        this.userService = userService;
    }

    // Get all lists of a specific user by ID
    @IsAdmin
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
