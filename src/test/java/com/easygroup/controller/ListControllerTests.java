package com.easygroup.controller;

import com.easygroup.dto.ListRequest;
import com.easygroup.dto.ListResponse;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.User;
import com.easygroup.service.ListService;
import com.easygroup.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListControllerTests {

    @Mock
    private ListService listService;

    @Mock
    private UserService userService; // Keep this if you plan to reintroduce a method using it

    @InjectMocks
    private ListController listController;

    private User testUser;
    private User otherUser;
    private ListEntity testList1;
    private ListEntity testList2;
    private ListEntity sharedList;
    private UUID userId;
    private UUID otherUserId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("user@test.com");
        testUser.setFirstName("Dodo");
        testUser.setLastName("Dada");

        otherUser = new User();
        otherUser.setId(otherUserId);
        otherUser.setEmail("other@test.com");
        otherUser.setFirstName("Lulu");
        otherUser.setLastName("Nunu");

        testList1 = new ListEntity();
        testList1.setId(UUID.randomUUID());
        testList1.setName("My First List");
        testList1.setIsShared(false);
        testList1.setUser(testUser);

        testList2 = new ListEntity();
        testList2.setId(UUID.randomUUID());
        testList2.setName("My Second List");
        testList2.setIsShared(false);
        testList2.setUser(testUser);

        sharedList = new ListEntity();
        sharedList.setId(UUID.randomUUID());
        sharedList.setName("Shared List");
        sharedList.setIsShared(true);
        sharedList.setUser(testUser);
    }

    @Test
    void createList_Success() {

        ListRequest request = new ListRequest();
        request.setName("New Test List");

        ListEntity savedList = new ListEntity();
        savedList.setId(UUID.randomUUID());
        savedList.setName("New Test List");
        savedList.setIsShared(false);
        savedList.setUser(testUser);

        when(listService.save(eq("New Test List"), eq(testUser))).thenReturn(savedList);

        ResponseEntity<ListResponse> result = listController.createList(request, testUser);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(savedList.getId(), result.getBody().getId());
        assertEquals("New Test List", result.getBody().getName());
        assertEquals(false, result.getBody().getIsShared());
    }

    @Test
    void createList_DuplicateName_ReturnsBadRequest() {

        ListRequest request = new ListRequest();
        request.setName("Duplicate List");

        when(listService.save(eq("Duplicate List"), eq(testUser)))
                .thenThrow(new IllegalArgumentException("List name already exists"));

        ResponseEntity<ListResponse> result = listController.createList(request, testUser);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void createList_ServiceError_ReturnsBadRequest() {

        ListRequest request = new ListRequest();
        request.setName("Error List");

        when(listService.save(eq("Error List"), eq(testUser)))
                .thenThrow(new IllegalArgumentException("Some validation error"));

        ResponseEntity<ListResponse> result = listController.createList(request, testUser);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void getListByUser_Success() {

        List<ListEntity> userLists = Arrays.asList(testList1, testList2, sharedList);
        when(listService.findByUser(testUser)).thenReturn(userLists);

        ResponseEntity<List<ListResponse>> result = listController.getListByUser(testUser);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(3, result.getBody().size());

        ListResponse response1 = result.getBody().get(0);
        assertEquals(testList1.getId(), response1.getId());
        assertEquals("My First List", response1.getName());
        assertEquals(false, response1.getIsShared());

        ListResponse sharedResponse = result.getBody().get(2);
        assertEquals(sharedList.getId(), sharedResponse.getId());
        assertEquals("Shared List", sharedResponse.getName());
        assertEquals(true, sharedResponse.getIsShared());
    }

    @Test
    void getListByUser_EmptyList_Success() {

        when(listService.findByUser(testUser)).thenReturn(Arrays.asList());

        ResponseEntity<List<ListResponse>> result = listController.getListByUser(testUser);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void getListByUser_ServiceError_ReturnsNotFound() {

        when(listService.findByUser(testUser)).thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<List<ListResponse>> result = listController.getListByUser(testUser);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void getListById_Success() {
        when(listService.findByIdAndUserId(testList1.getId(), testUser.getId())).thenReturn(testList1);

        ResponseEntity<ListResponse> result = listController.getListById(testList1.getId(), testUser);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(testList1.getId(), result.getBody().getId());
        assertEquals(testList1.getName(), result.getBody().getName());
        assertEquals(testList1.getIsShared(), result.getBody().getIsShared());
    }

    @Test
    void getListById_NotFoundOrNotOwned_ReturnsForbidden() {
        when(listService.findByIdAndUserId(testList1.getId(), testUser.getId())).thenReturn(null);

        ResponseEntity<ListResponse> result = listController.getListById(testList1.getId(), testUser);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertNull(result.getBody());
    }
}