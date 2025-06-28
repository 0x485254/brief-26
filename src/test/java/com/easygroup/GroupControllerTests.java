package com.easygroup;

import com.easygroup.dto.GroupResponse;
import com.easygroup.dto.PersonResponse;
import com.easygroup.entity.Person;
import com.easygroup.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class GroupControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    private GroupResponse testGroupResponse;
    private PersonResponse testPersonResponse;
    private UUID drawId;
    private UUID groupId;
    private UUID personId;

    @BeforeEach
    void setUp() {
        drawId = UUID.randomUUID();
        groupId = UUID.randomUUID();
        personId = UUID.randomUUID();

        testPersonResponse = new PersonResponse();
        testPersonResponse.setPersonId(personId);
        testPersonResponse.setName("John Doe");
        testPersonResponse.setAge(25);
        testPersonResponse.setGender(Person.Gender.MALE.toString());
        testPersonResponse.setFrenchLevel(3);
        testPersonResponse.setTechLevel(4);
        testPersonResponse.setOldDwwm(false);
        testPersonResponse.setProfile(Person.Profile.A_LAISE.toString());

        testGroupResponse = new GroupResponse();
        testGroupResponse.setId(groupId);
        testGroupResponse.setName("Group A");
        testGroupResponse.setDrawId(drawId);
        testGroupResponse.setPersons(Arrays.asList(testPersonResponse));
    }

    @Test
    @WithMockUser
    void getGroupsForDraw_Success() throws Exception {
        GroupResponse group1 = new GroupResponse();
        group1.setId(UUID.randomUUID());
        group1.setName("Group A");
        group1.setDrawId(drawId);
        group1.setPersons(Arrays.asList(testPersonResponse));

        GroupResponse group2 = new GroupResponse();
        group2.setId(UUID.randomUUID());
        group2.setName("Group B");
        group2.setDrawId(drawId);
        group2.setPersons(Arrays.asList());

        java.util.List<GroupResponse> groups = Arrays.asList(group1, group2);

        when(groupService.getGroupsByDrawId(any(UUID.class)))
                .thenReturn(groups);

        mockMvc.perform(get("/api/draws/{drawId}/groups", drawId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Group A"))
                .andExpect(jsonPath("$[0].persons.length()").value(1))
                .andExpect(jsonPath("$[0].persons[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Group B"))
                .andExpect(jsonPath("$[1].persons.length()").value(0));
    }

    @Test
    @WithMockUser
    void getGroupsForDraw_EmptyResult() throws Exception {
        when(groupService.getGroupsByDrawId(any(UUID.class)))
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/draws/{drawId}/groups", drawId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void getGroupsForDraw_NotFound_DrawDoesNotExist() throws Exception {
        when(groupService.getGroupsByDrawId(any(UUID.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Draw not found"));

        mockMvc.perform(get("/api/draws/{drawId}/groups", drawId))
                .andExpect(status().isNotFound());
    }


    @Test
    void getGroupsForDraw_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/draws/{drawId}/groups", drawId))
                .andExpect(status().isForbidden());
    }

}