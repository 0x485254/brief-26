package com.easygroup.controller;

import com.easygroup.entity.ListEntity;
import com.easygroup.entity.ListShare;
import com.easygroup.entity.User;
import com.easygroup.service.ListService;
import com.easygroup.service.ListShareService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/lists/{listId}/share")
public class ListShareController {

    private final ListShareService listShareService;
    private final ListService listService;

    public ListShareController(ListShareService listShareService, ListService listService){
        this.listShareService = listShareService;
        this.listService = listService;
    }

    //share a list to a user
    @PostMapping
    public ResponseEntity<ListShare> shareList(@AuthenticationPrincipal User owner, @RequestBody User userSharedTo, @PathVariable UUID listId){
        ListEntity list = this.listService.findById(listId).orElseThrow();

        if (owner.getLists().stream().anyMatch(l -> l.equals(list))){
            ListShare listShared = this.listShareService.shareList(list, userSharedTo);

            return ResponseEntity.ok(listShared);
        }
        return ResponseEntity.notFound().build();
    }

    //Unshare a list to a user
    @DeleteMapping("/{sharedUserId}")
    public ResponseEntity<ListEntity> deleteListShare(@AuthenticationPrincipal User owner, @RequestBody User userUnshareTo, @PathVariable UUID listId){
        ListEntity list = this.listService.findById(listId).orElseThrow();

        if (owner.getLists().stream().anyMatch(l -> l.equals(list))){
            ListEntity listUnshared = this.listShareService.unshareList(list, userUnshareTo);

            return ResponseEntity.ok(listUnshared);
        }
        return ResponseEntity.notFound().build();


    }





}
