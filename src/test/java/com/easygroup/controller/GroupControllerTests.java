package com.easygroup.controller;

import com.easygroup.dto.GroupResponse;
import com.easygroup.dto.PersonResponse;
import com.easygroup.entity.Group;
import com.easygroup.service.GroupService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTests {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private UUID drawId;
    private UUID groupId;
    private UUID personId;
    private Group testGroup;
    private GroupResponse groupResponse1;
    private GroupResponse groupResponse2;

    @BeforeEach
    void setUp() {
        drawId = UUID.randomUUID();
        groupId = UUID.randomUUID();
        personId = UUID.randomUUID();

        testGroup = new Group();
        testGroup.setId(groupId);
        testGroup.setName("Barcelona");

        PersonResponse messi = new PersonResponse();
        messi.setPersonId(UUID.randomUUID());
        messi.setName("Lionel Messi");
        messi.setGender("MALE");
        messi.setAge(36);
        messi.setFrenchLevel(2);
        messi.setOldDwwm(false);
        messi.setTechLevel(5);
        messi.setProfile("A_LAISE");

        PersonResponse neymar = new PersonResponse();
        neymar.setPersonId(UUID.randomUUID());
        neymar.setName("Neymar Jr");
        neymar.setGender("MALE");
        neymar.setAge(31);
        neymar.setFrenchLevel(3);
        neymar.setOldDwwm(true);
        neymar.setTechLevel(4);
        neymar.setProfile("RESERVE");

        PersonResponse mbappe = new PersonResponse();
        mbappe.setPersonId(UUID.randomUUID());
        mbappe.setName("Kylian Mbappé");
        mbappe.setGender("MALE");
        mbappe.setAge(25);
        mbappe.setFrenchLevel(5);
        mbappe.setOldDwwm(false);
        mbappe.setTechLevel(4);
        mbappe.setProfile("A_LAISE");

        groupResponse1 = GroupResponse.builder()
                .id(UUID.randomUUID())
                .name("Barcelona")
                .drawId(drawId)
                .persons(Arrays.asList(messi, neymar))
                .personCount(2)
                .build();

        groupResponse2 = GroupResponse.builder()
                .id(UUID.randomUUID())
                .name("PSG")
                .drawId(drawId)
                .persons(Arrays.asList(mbappe))
                .personCount(1)
                .build();
    }

    @Test
    void getGroupsForDraw_Success() {
        List<GroupResponse> groups = Arrays.asList(groupResponse1, groupResponse2);
        when(groupService.getGroupsByDrawId(drawId)).thenReturn(groups);

        ResponseEntity<List<GroupResponse>> result = groupController.getGroupsForDraw(drawId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
        assertEquals("Barcelona", result.getBody().get(0).getName());
        assertEquals("PSG", result.getBody().get(1).getName());
        assertEquals(2, result.getBody().get(0).getPersonCount());
        assertEquals(1, result.getBody().get(1).getPersonCount());
    }

    @Test
    void getGroupsForDraw_EmptyResult_Success() {
        when(groupService.getGroupsByDrawId(drawId)).thenReturn(Arrays.asList());

        ResponseEntity<List<GroupResponse>> result = groupController.getGroupsForDraw(drawId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void addPersonToGroup_Success() {
        doNothing().when(groupService).addPersonToGroup(groupId, personId);

        ResponseEntity<Void> result = groupController.addPersonToGroup(groupId, personId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(groupService).addPersonToGroup(groupId, personId);
    }

    @Test
    void removePersonFromGroup_Success() {
        doNothing().when(groupService).removePersonFromGroup(groupId, personId);

        ResponseEntity<Void> result = groupController.removePersonFromGroup(groupId, personId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(groupService).removePersonFromGroup(groupId, personId);
    }

    @Test
    void createGroup_Success() {
        Group newGroup = new Group();
        newGroup.setName("Real Madrid");

        when(groupService.createGroup(any(Group.class), eq(drawId))).thenReturn(testGroup);

        ResponseEntity<Void> result = groupController.createGroup(drawId, newGroup);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNull(result.getBody());
        verify(groupService).createGroup(newGroup, drawId);
    }

    @Test
    void updateGroup_Success() {
        Group updatedGroup = new Group();
        updatedGroup.setId(groupId);
        updatedGroup.setName("FC Barcelona");

        when(groupService.updateGroup(eq(groupId), any(Group.class))).thenReturn(updatedGroup);

        ResponseEntity<Group> result = groupController.updateGroup(groupId, updatedGroup);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("FC Barcelona", result.getBody().getName());
        verify(groupService).updateGroup(groupId, updatedGroup);
    }

    @Test
    void deleteGroup_Success() {
        when(groupService.deleteGroup(groupId)).thenReturn(true);

        ResponseEntity<Void> result = groupController.deleteGroup(groupId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(groupService).deleteGroup(groupId);
    }

    @Test
    void deleteGroup_NotFound() {
        when(groupService.deleteGroup(groupId)).thenReturn(false);

        ResponseEntity<Void> result = groupController.deleteGroup(groupId);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(groupService).deleteGroup(groupId);
    }
}