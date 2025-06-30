//package com.easygroup;
//
//import com.easygroup.controller.PersonController;
//import com.easygroup.dto.PersonRequest;
//import com.easygroup.entity.Person;
//import com.easygroup.service.PersonService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.doThrow;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ActiveProfiles("test") //tests are done with h2 Driver
//@SpringBootTest
//@AutoConfigureMockMvc
//class PersonControllerTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private PersonService personService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private Person testPerson;
//    private PersonRequest personRequest;
//    private UUID userId;
//    private UUID listId;
//    private UUID personId;
//
//    @BeforeEach
//    void setUp() {
//        userId = UUID.randomUUID();
//        listId = UUID.randomUUID();
//        personId = UUID.randomUUID();
//
//        // Initialize test person
//        testPerson = new Person();
//        testPerson.setId(personId);
//        testPerson.setName("John Doe");
//        testPerson.setAge(25);
//        testPerson.setGender(Person.Gender.MALE);
//        testPerson.setFrenchLevel(3);
//        testPerson.setTechLevel(4);
//        testPerson.setOldDwwm(false);
//        testPerson.setProfile(Person.Profile.A_LAISE);
//
//        // Initialize person request
//        personRequest = new PersonRequest();
//        personRequest.setName("John Doe");
//        personRequest.setAge(25);
//        personRequest.setGender(Person.Gender.MALE);
//        personRequest.setFrenchLevel(3);
//        personRequest.setTechLevel(4);
//        personRequest.setOldDwwm(false);
//        personRequest.setProfile(Person.Profile.TIMIDE);
//    }
//
//    @Test
//    @WithMockUser
//    void addPersonToList_Success() throws Exception {
//        when(personService.save(
//                any(Integer.class), any(Integer.class), any(Person.Gender.class),
//                any(String.class), any(Boolean.class), any(Person.Profile.class),
//                any(Integer.class), any(UUID.class)
//        )).thenReturn(testPerson);
//
//        mockMvc.perform(post("/users/{userId}/lists/{listId}/persons", userId, listId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(personRequest)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.name").value(testPerson.getName()))
//                .andExpect(jsonPath("$.age").value(testPerson.getAge()));
//    }
//
//    @Test
//    @WithMockUser
//    void addPersonToList_Conflict() throws Exception {
//        when(personService.save(
//                any(Integer.class), any(Integer.class), any(Person.Gender.class),
//                any(String.class), any(Boolean.class), any(Person.Profile.class),
//                any(Integer.class), any(UUID.class)
//        )).thenThrow(new IllegalArgumentException());
//
//        mockMvc.perform(post("/users/{userId}/lists/{listId}/persons", userId, listId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(personRequest)))
//                .andExpect(status().isConflict());
//    }
//
//    @Test
//    @WithMockUser
//    void getPersonsByList_Success() throws Exception {
//        when(personService.findByListIdOrderByName(any(UUID.class)))
//                .thenReturn(Arrays.asList(testPerson));
//
//        mockMvc.perform(get("/users/{userId}/lists/{listId}/persons", userId, listId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value(testPerson.getName()))
//                .andExpect(jsonPath("$[0].age").value(testPerson.getAge()));
//    }
//
//    @Test
//    @WithMockUser
//    void getPersonsByList_NotFound() throws Exception {
//        when(personService.findByListIdOrderByName(any(UUID.class)))
//                .thenThrow(new IllegalArgumentException());
//
//        mockMvc.perform(get("/users/{userId}/lists/{listId}/persons", userId, listId))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @WithMockUser
//    void editPerson_Success() throws Exception {
//        when(personService.edit(
//                any(UUID.class), any(Integer.class), any(Integer.class),
//                any(Person.Gender.class), any(String.class), any(Boolean.class),
//                any(Person.Profile.class), any(Integer.class)
//        )).thenReturn(testPerson);
//
//        mockMvc.perform(put("/users/{userId}/lists/{listId}/persons/{personId}", userId, listId, personId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(personRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value(testPerson.getName()))
//                .andExpect(jsonPath("$.age").value(testPerson.getAge()));
//    }
//
//    @Test
//    @WithMockUser
//    void editPerson_NotFound() throws Exception {
//        when(personService.edit(
//                any(UUID.class), any(Integer.class), any(Integer.class),
//                any(Person.Gender.class), any(String.class), any(Boolean.class),
//                any(Person.Profile.class), any(Integer.class)
//        )).thenThrow(new IllegalArgumentException());
//
//        mockMvc.perform(put("/users/{userId}/lists/{listId}/persons/{personId}", userId, listId, personId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(personRequest)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @WithMockUser
//    void deletePerson_Success() throws Exception {
//        mockMvc.perform(delete("/users/{userId}/lists/{listId}/persons/{personId}", userId, listId, personId))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser
//    void deletePerson_NotFound() throws Exception {
//        doThrow(new IllegalArgumentException())
//                .when(personService).deleteById(any(UUID.class));
//
//        mockMvc.perform(delete("/users/{userId}/lists/{listId}/persons/{personId}", userId, listId, personId))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void unauthorizedAccess() throws Exception {
//        mockMvc.perform(get("/users/{userId}/lists/{listId}/persons", userId, listId))
//                .andExpect(status().isForbidden());
//    }
//}