package com.easygroup;

import com.easygroup.controller.ListController;
import com.easygroup.dto.ListRequest;
import com.easygroup.dto.ListResponse;
import com.easygroup.entity.List;
import com.easygroup.entity.User;
import com.easygroup.service.ListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test") //tests are done with h2 Driver
@SpringBootTest
@AutoConfigureMockMvc
public class ListControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListService listService;

    @Autowired
    private ObjectMapper objectMapper;

    private List testList;
    private ListRequest listRequest;
    private User testUser;
    private UUID listId;

    @BeforeEach
    void setUp() {
        listId = UUID.randomUUID();

        // Initialize test list
        testList = new List();
        testList.setId(listId);
        testList.setName("Test List");
        testList.setIsShared(false);

        // Initialize list request
        listRequest = new ListRequest();
        listRequest.setName("Test List");

        // Initialize test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
    }

    //Test create list with success
    @Test
    @WithMockUser
    void createList_Success() throws Exception {
        when(listService.save(any(String.class), any(User.class))).thenReturn(testList);

        mockMvc.perform(post("/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(listRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testList.getName()))
                .andExpect(jsonPath("$.isShared").value(testList.getIsShared()));
    }

    //Test create list with conflict
    @Test
    @WithMockUser
    void createList_BadRequest() throws Exception {
        when(listService.save(any(String.class), any(User.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(listRequest)))
                .andExpect(status().isBadRequest());
    }

    //Test get list by id with success
    @Test
    @WithMockUser
    void getListByUser_Success() throws Exception {
        when(listService.findByUser(any(User.class))).thenReturn(Arrays.asList(testList));

        mockMvc.perform(get("/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(testList.getName()))
                .andExpect(jsonPath("$[0].isShared").value(testList.getIsShared()));
    }

    //Test get list by id with conflict
    @Test
    @WithMockUser
    void getListByUser_NotFound() throws Exception {
        when(listService.findByUser(any(User.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/lists"))
                .andExpect(status().isNotFound());
    }

    //Test unauthorized access to get list by id
    @Test
    void unauthorizedAccess() throws Exception {
        mockMvc.perform(get("/lists"))
                .andExpect(status().isForbidden());
    }
}