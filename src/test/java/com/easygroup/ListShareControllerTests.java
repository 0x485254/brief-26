package com.easygroup;

import com.easygroup.controller.ListShareController;
import com.easygroup.entity.List;
import com.easygroup.entity.ListShare;
import com.easygroup.entity.User;
import com.easygroup.service.ListService;
import com.easygroup.service.ListShareService;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test") //tests are done with h2 Driver
@SpringBootTest
@AutoConfigureMockMvc
public class ListShareControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListShareService listShareService;

    @MockBean
    private ListService listService;

    @Autowired
    private ObjectMapper objectMapper;

    private List testList;
    private User testOwner;
    private User testUserSharedTo;
    private ListShare testListShare;
    private UUID listId;
    private UUID sharedUserId;

    @BeforeEach
    void setUp() {
        listId = UUID.randomUUID();
        sharedUserId = UUID.randomUUID();

        // Initialize test list
        testList = new List();
        testList.setId(listId);
        testList.setName("Test List");
        testList.setIsShared(true);

        // Initialize test users
        testOwner = new User();
        testOwner.setId(UUID.randomUUID());
        testOwner.setLists(Arrays.asList(testList));

        testUserSharedTo = new User();
        testUserSharedTo.setId(sharedUserId);

        // Initialize test list share
        testListShare = new ListShare();
        testListShare.setId(UUID.randomUUID());
        testListShare.setList(testList);
    }

    //Test share list with success
    @Test
    @WithMockUser
    void shareList_Success() throws Exception {
        when(listService.findById(any(UUID.class))).thenReturn(Optional.of(testList));
        when(listShareService.shareList(any(List.class), any(User.class))).thenReturn(testListShare);

        mockMvc.perform(post("/lists/{listId}/share", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserSharedTo)))
                .andExpect(status().isOk());
    }

    //Test share list with conflict
    @Test
    @WithMockUser
    void shareList_NotFound() throws Exception {
        when(listService.findById(any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/lists/{listId}/share", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserSharedTo)))
                .andExpect(status().isNotFound());
    }

    //Test delete list share with success
    @Test
    @WithMockUser
    void deleteListShare_Success() throws Exception {
        when(listService.findById(any(UUID.class))).thenReturn(Optional.of(testList));
        when(listShareService.unshareList(any(List.class), any(User.class))).thenReturn(testList);

        mockMvc.perform(delete("/lists/{listId}/share/{sharedUserId}", listId, sharedUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserSharedTo)))
                .andExpect(status().isOk());
    }

    //Test delete list share with conflict
    @Test
    @WithMockUser
    void deleteListShare_NotFound() throws Exception {
        when(listService.findById(any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(delete("/lists/{listId}/share/{sharedUserId}", listId, sharedUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserSharedTo)))
                .andExpect(status().isNotFound());
    }

    //Test unauthorized access to share list
    @Test
    void unauthorizedAccess() throws Exception {
        mockMvc.perform(post("/lists/{listId}/share", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserSharedTo)))
                .andExpect(status().isForbidden());
    }
}