package com.easygroup.controller;

import com.easygroup.dto.DrawResponse;
import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.dto.GroupPreviewResponse;
import com.easygroup.dto.GroupResponse;
import com.easygroup.entity.User;
import com.easygroup.service.DrawService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DrawControllerTests {

    @Mock
    private DrawService drawService;

    @InjectMocks
    private DrawController drawController;

    private User testUser;
    private UUID userId;
    private UUID listId;
    private GenerateGroupsRequest generateRequest;
    private GroupPreviewResponse previewResponse;
    private DrawResponse drawResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        listId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("user@test.com");
        testUser.setFirstName("Dodo");
        testUser.setLastName("Nana");

        generateRequest = GenerateGroupsRequest.builder()
                .title("Tessssssst Groups")
                .numberOfGroups(3)
                .groupNames(Arrays.asList("Group 1", "Group 2", "Group 3"))
                .balanceByGender(true)
                .balanceByAge(false)
                .balanceByFrenchLevel(true)
                .balanceByTechLevel(false)
                .balanceByOldDwwm(true)
                .balanceByProfile(false)
                .build();

        GroupResponse group1 = GroupResponse.builder()
                .id(UUID.randomUUID())
                .name("Group 1")
                .personCount(3)
                .build();

        GroupResponse group2 = GroupResponse.builder()
                .id(UUID.randomUUID())
                .name("Group 2")
                .personCount(3)
                .build();

        GroupResponse group3 = GroupResponse.builder()
                .id(UUID.randomUUID())
                .name("Group 3")
                .personCount(2)
                .build();

        previewResponse = GroupPreviewResponse.builder()
                .listId(listId)
                .listName("Tessssssst List")
                .title("Tessssssst Groups")
                .groups(Arrays.asList(group1, group2, group3))
                .groupCount(3)
                .totalPersons(8)
                .generatedAt(LocalDateTime.now())
                .build();

        drawResponse = DrawResponse.builder()
                .id(UUID.randomUUID())
                .title("Tessssssst Groups")
                .createdAt(LocalDateTime.now())
                .listId(listId)
                .listName("Tessssssst List")
                .groupCount(3)
                .groups(Arrays.asList(group1, group2, group3))
                .build();
    }

    @Test
    void generatePreview_Success() {
        when(drawService.generatePreview(any(GenerateGroupsRequest.class), eq(userId), eq(listId)))
                .thenReturn(previewResponse);

        ResponseEntity<GroupPreviewResponse> result = drawController.generatePreview(testUser, listId, generateRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(previewResponse.getListId(), result.getBody().getListId());
        assertEquals(previewResponse.getTitle(), result.getBody().getTitle());
        assertEquals(previewResponse.getGroupCount(), result.getBody().getGroupCount());
        assertEquals(previewResponse.getTotalPersons(), result.getBody().getTotalPersons());
        assertEquals(3, result.getBody().getGroups().size());
    }

    @Test
    void generatePreview_WithMinimalRequest_Success() {
        GenerateGroupsRequest minimalRequest = GenerateGroupsRequest.builder()
                .numberOfGroups(2)
                .groupNames(Arrays.asList("Team A", "Team B"))
                .build();

        GroupPreviewResponse minimalPreview = GroupPreviewResponse.builder()
                .listId(listId)
                .listName("Tessssssst List")
                .title("Groups for Tessssssst List - 2025-07-03 10:30")
                .groups(Arrays.asList(
                        GroupResponse.builder().id(UUID.randomUUID()).name("Team A").personCount(4).build(),
                        GroupResponse.builder().id(UUID.randomUUID()).name("Team B").personCount(4).build()))
                .groupCount(2)
                .totalPersons(8)
                .generatedAt(LocalDateTime.now())
                .build();

        when(drawService.generatePreview(any(GenerateGroupsRequest.class), eq(userId), eq(listId)))
                .thenReturn(minimalPreview);

        ResponseEntity<GroupPreviewResponse> result = drawController.generatePreview(testUser, listId, minimalRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().getGroupCount());
        assertEquals(8, result.getBody().getTotalPersons());
    }

    @Test
    void saveGroups_Success() {
        when(drawService.saveModifiedGroups(any(GroupPreviewResponse.class), eq(userId), eq(listId)))
                .thenReturn(drawResponse);

        ResponseEntity<DrawResponse> result = drawController.saveGroups(testUser, listId, previewResponse);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(drawResponse.getId(), result.getBody().getId());
        assertEquals(drawResponse.getTitle(), result.getBody().getTitle());
        assertEquals(drawResponse.getListId(), result.getBody().getListId());
        assertEquals(drawResponse.getGroupCount(), result.getBody().getGroupCount());
    }

    @Test
    void saveGroups_WithModifiedPreview_Success() {
        GroupPreviewResponse modifiedPreview = GroupPreviewResponse.builder()
                .listId(listId)
                .listName("Tessssssst List")
                .title("Modified Tessssssst Groups")
                .groups(Arrays.asList(
                        GroupResponse.builder().id(UUID.randomUUID()).name("Group 1").personCount(4).build(),
                        GroupResponse.builder().id(UUID.randomUUID()).name("Group 2").personCount(2).build(),
                        GroupResponse.builder().id(UUID.randomUUID()).name("Group 3").personCount(2).build()))
                .groupCount(3)
                .totalPersons(8)
                .generatedAt(LocalDateTime.now())
                .build();

        DrawResponse modifiedDrawResponse = DrawResponse.builder()
                .id(UUID.randomUUID())
                .title("Modified Tessssssst Groups")
                .createdAt(LocalDateTime.now())
                .listId(listId)
                .listName("Tessssssst List")
                .groupCount(3)
                .build();

        when(drawService.saveModifiedGroups(any(GroupPreviewResponse.class), eq(userId), eq(listId)))
                .thenReturn(modifiedDrawResponse);

        ResponseEntity<DrawResponse> result = drawController.saveGroups(testUser, listId, modifiedPreview);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Modified Tessssssst Groups", result.getBody().getTitle());
    }

    @Test
    void getDrawsForList_Success() {
        DrawResponse draw1 = DrawResponse.builder()
                .id(UUID.randomUUID())
                .title("First Draw")
                .createdAt(LocalDateTime.now().minusDays(2))
                .listId(listId)
                .listName("Tessssssst List")
                .groupCount(3)
                .build();

        DrawResponse draw2 = DrawResponse.builder()
                .id(UUID.randomUUID())
                .title("Second Draw")
                .createdAt(LocalDateTime.now().minusDays(1))
                .listId(listId)
                .listName("Tessssssst List")
                .groupCount(2)
                .build();

        List<DrawResponse> draws = Arrays.asList(draw2, draw1);

        when(drawService.getDrawsForList(eq(userId), eq(listId))).thenReturn(draws);

        ResponseEntity<List<DrawResponse>> result = drawController.getDrawsForList(testUser, listId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        assertEquals("Second Draw", result.getBody().get(0).getTitle());
        assertEquals("First Draw", result.getBody().get(1).getTitle());
    }

    @Test
    void getDrawsForList_EmptyList_Success() {
        when(drawService.getDrawsForList(eq(userId), eq(listId))).thenReturn(Arrays.asList());

        ResponseEntity<List<DrawResponse>> result = drawController.getDrawsForList(testUser, listId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void getDrawsForList_SingleDraw_Success() {
        List<DrawResponse> singleDraw = Arrays.asList(drawResponse);
        when(drawService.getDrawsForList(eq(userId), eq(listId))).thenReturn(singleDraw);

        ResponseEntity<List<DrawResponse>> result = drawController.getDrawsForList(testUser, listId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(drawResponse.getTitle(), result.getBody().get(0).getTitle());
    }
}