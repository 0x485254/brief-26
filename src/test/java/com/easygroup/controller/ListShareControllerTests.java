package com.easygroup.controller;

import com.easygroup.dto.ShareListRequest;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.ListShare;
import com.easygroup.entity.User;
import com.easygroup.service.ListService;
import com.easygroup.service.ListShareService;
import com.easygroup.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListShareControllerTests {

    @Mock
    private ListShareService listShareService;

    @Mock
    private ListService listService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ListShareController listShareController;

    private User testOwner;
    private User testUserSharedTo;
    private ListEntity testList;
    private ListShare testListShare;
    private UUID listId;
    private UUID ownerId;
    private UUID sharedUserId;

    @BeforeEach
    void setUp() {
        listId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        sharedUserId = UUID.randomUUID();

        testOwner = new User();
        testOwner.setId(ownerId);
        testOwner.setEmail("owner@test.com");

        testList = new ListEntity();
        testList.setId(listId);
        testList.setName("Test List");
        testList.setUser(testOwner);

        testUserSharedTo = new User();
        testUserSharedTo.setId(sharedUserId);
        testUserSharedTo.setEmail("shared@test.com");

        testListShare = new ListShare();
        testListShare.setId(UUID.randomUUID());
        testListShare.setList(testList);
        testListShare.setSharedWithUser(testUserSharedTo);
    }

    @Test
    void shareList_WithUserId_Success() {
        ShareListRequest request = new ShareListRequest(sharedUserId, null);

        when(listService.findByIdAndUserId(listId, ownerId)).thenReturn(testList);
        when(userService.findById(sharedUserId)).thenReturn(Optional.of(testUserSharedTo));
        when(listShareService.shareList(testList, testUserSharedTo)).thenReturn(testListShare);

        ResponseEntity<ListShare> result = listShareController.shareList(testOwner, request, listId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(testListShare.getId(), result.getBody().getId());
    }

    @Test
    void shareList_WithEmail_Success() {
        ShareListRequest request = new ShareListRequest(null, "shared@test.com");

        when(listService.findByIdAndUserId(listId, ownerId)).thenReturn(testList);
        when(userService.findByEmail("shared@test.com")).thenReturn(Optional.of(testUserSharedTo));
        when(listShareService.shareList(testList, testUserSharedTo)).thenReturn(testListShare);

        ResponseEntity<ListShare> result = listShareController.shareList(testOwner, request, listId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(testListShare.getId(), result.getBody().getId());
    }

    @Test
    void shareList_ListNotOwned_ReturnsForbidden() {
        ShareListRequest request = new ShareListRequest(sharedUserId, null);

        when(listService.findByIdAndUserId(listId, ownerId)).thenReturn(null);

        ResponseEntity<ListShare> result = listShareController.shareList(testOwner, request, listId);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void shareList_UserNotFound_ReturnsNotFound() {
        ShareListRequest request = new ShareListRequest(sharedUserId, null);

        when(listService.findByIdAndUserId(listId, ownerId)).thenReturn(testList);
        when(userService.findById(sharedUserId)).thenReturn(Optional.empty());

        ResponseEntity<ListShare> result = listShareController.shareList(testOwner, request, listId);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void shareList_EmailNotFound_ReturnsNotFound() {
        ShareListRequest request = new ShareListRequest(null, "notfound@test.com");

        when(listService.findByIdAndUserId(listId, ownerId)).thenReturn(testList);
        when(userService.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        ResponseEntity<ListShare> result = listShareController.shareList(testOwner, request, listId);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void shareList_BothUserIdAndEmailNull_ReturnsNotFound() {
        ShareListRequest request = new ShareListRequest(null, null);

        when(listService.findByIdAndUserId(listId, ownerId)).thenReturn(testList);

        ResponseEntity<ListShare> result = listShareController.shareList(testOwner, request, listId);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void deleteListShare_Success() {
        User userToUnshare = new User();
        userToUnshare.setId(sharedUserId);

        testOwner.setLists(List.of(testList));

        when(listService.findById(listId)).thenReturn(Optional.of(testList));
        when(listShareService.unshareList(testList, userToUnshare)).thenReturn(testList);

        ResponseEntity<ListEntity> result = listShareController.deleteListShare(testOwner, userToUnshare, listId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(testList.getId(), result.getBody().getId());
    }

    @Test
    void deleteListShare_UserNotOwner_ReturnsNotFound() {
        User userToUnshare = new User();
        userToUnshare.setId(sharedUserId);

        testOwner.setLists(List.of());

        when(listService.findById(listId)).thenReturn(Optional.of(testList));

        ResponseEntity<ListEntity> result = listShareController.deleteListShare(testOwner, userToUnshare, listId);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }
}