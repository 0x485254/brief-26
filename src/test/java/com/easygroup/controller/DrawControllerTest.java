// package com.easygroup.controller;

// import com.easygroup.dto.*;
// import com.easygroup.service.DrawService;
// import com.easygroup.service.GroupService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MvcResult;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;
// import java.util.UUID;

// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
// import static
// org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static
// org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @ExtendWith(MockitoExtension.class)
// class DrawControllerTest {

// @Mock
// private DrawService drawService;

// @Mock
// private GroupService groupService;

// @InjectMocks
// private DrawController drawController;

// private MockMvc mockMvc;
// private ObjectMapper objectMapper;
// private UUID listId;
// private GenerateGroupsRequest generateRequest;

// @BeforeEach
// void setUp() {
// mockMvc = MockMvcBuilders.standaloneSetup(drawController).build();

// objectMapper = new ObjectMapper();
// objectMapper.registerModule(new JavaTimeModule());

// listId = UUID.randomUUID();

// generateRequest = GenerateGroupsRequest.builder()
// .title("Groupes interpromo")
// .numberOfGroups(3)
// .groupNames(Arrays.asList("Groupe A", "Groupe B", "Groupe C"))
// .balanceByGender(true)
// .balanceByAge(true)
// .balanceByFrenchLevel(true)
// .balanceByTechLevel(true)
// .balanceByOldDwwm(false)
// .balanceByProfile(true)
// .build();
// }

// @Test
// void testCompleteWorkflow_PreviewThenSave() throws Exception {

// System.out.println("\n STEP 1: Generate Preview (No Save)");
// System.out.println("-".repeat(50));

// GroupPreviewResponse previewResponse = createPreviewResponse();

// lenient().when(drawService.generatePreview(any(), any(), any()))
// .thenReturn(previewResponse);

// System.out.println("Request: POST /api/lists/" + listId +
// "/draws?save=false");
// System.out.println("Generation Request:");
// System.out.println(" Title: " + generateRequest.getTitle());
// System.out.println(" Groups: " + generateRequest.getNumberOfGroups());
// System.out.println(" Balance by Gender: " +
// generateRequest.getBalanceByGender());
// System.out.println(" Balance by Age: " + generateRequest.getBalanceByAge());
// System.out.println(" Balance by French Level: " +
// generateRequest.getBalanceByFrenchLevel());
// System.out.println(" Balance by Tech Level: " +
// generateRequest.getBalanceByTechLevel());
// System.out.println(" Balance by Profile: " +
// generateRequest.getBalanceByProfile());

// MvcResult previewResult = mockMvc.perform(post("/api/lists/{listId}/draws",
// listId)
// .param("save", "false")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(generateRequest)))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$.groups[0].id").doesNotExist())
// .andExpect(jsonPath("$.groups[0].drawId").doesNotExist())
// .andReturn();

// displayPreviewResults(previewResult);

// System.out.println("\n STEP 2: Save Preview to Database");
// System.out.println("-".repeat(50));

// DrawResponse saveResponse = createSaveResponse();

// lenient().when(drawService.savePreviewGroups(any(), any(), any()))
// .thenReturn(saveResponse);

// System.out.println("Request: POST /api/lists/" + listId +
// "/draws?save=true");

// MvcResult saveResult = mockMvc.perform(post("/api/lists/{listId}/draws",
// listId)
// .param("save", "true")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(generateRequest)))
// .andExpect(status().isCreated())
// .andExpect(jsonPath("$.groups[0].id").exists())
// .andExpect(jsonPath("$.groups[0].drawId").exists())
// .andReturn();

// displaySaveResults(saveResult);
// }

// private GroupPreviewResponse createPreviewResponse() {
// List<PersonResponse> alphaTeam = Arrays.asList(
// new PersonResponse(UUID.randomUUID(), "Mbappe Killian", "FEMALE", 24, 4,
// false, 3,
// "A_LAISE"),
// new PersonResponse(UUID.randomUUID(), "Thomas Mular", "MALE", 28, 3, true, 4,
// "RESERVE"),
// new PersonResponse(UUID.randomUUID(), "Luka Modric", "FEMALE", 22, 5, false,
// 2,
// "A_LAISE"));

// List<PersonResponse> betaTeam = Arrays.asList(
// new PersonResponse(UUID.randomUUID(), "Lucas Hernandez", "MALE", 26, 2,
// false, 3,
// "TIMIDE"),
// new PersonResponse(UUID.randomUUID(), "Sophie Laurance", "FEMALE", 30, 4,
// true, 4,
// "A_LAISE"),
// new PersonResponse(UUID.randomUUID(), "Julien Robert", "MALE", 25, 3, false,
// 2,
// "RESERVE"));

// List<PersonResponse> gammaTeam = Arrays.asList(
// new PersonResponse(UUID.randomUUID(), "Gal Gadot", "FEMALE", 27, 3, false, 4,
// "A_LAISE"),
// new PersonResponse(UUID.randomUUID(), "Antoine Grizman", "MALE", 23, 4,
// false, 3,
// "RESERVE"),
// new PersonResponse(UUID.randomUUID(), "Pique Girard", "FEMALE", 29, 2, true,
// 3,
// "TIMIDE"));

// List<GroupResponse> groups = Arrays.asList(
// GroupResponse.builder()
// .id(null)
// .name("Groupe A")
// .drawId(null)
// .persons(alphaTeam)
// .personCount(alphaTeam.size())
// .build(),
// GroupResponse.builder()
// .id(null)
// .name("Groupe B")
// .drawId(null)
// .persons(betaTeam)
// .personCount(betaTeam.size())
// .build(),
// GroupResponse.builder()
// .id(null)
// .name("Groupe C")
// .drawId(null)
// .persons(gammaTeam)
// .personCount(gammaTeam.size())
// .build());

// return GroupPreviewResponse.builder()
// .listId(listId)
// .listName("Promo CDA - Mai 2026")
// .title("Groupes interpromo")
// .groups(groups)
// .groupCount(3)
// .totalPersons(9)
// .generatedAt(LocalDateTime.now())
// .build();
// }

// private DrawResponse createSaveResponse() {
// UUID drawId = UUID.randomUUID();

// List<PersonResponse> alphaTeam = Arrays.asList(
// new PersonResponse(UUID.randomUUID(), "Mbappe Killian", "FEMALE", 24, 4,
// false, 3,
// "A_LAISE"),
// new PersonResponse(UUID.randomUUID(), "Thomas Mular", "MALE", 28, 3, true, 4,
// "RESERVE"),
// new PersonResponse(UUID.randomUUID(), "Luka Modric", "FEMALE", 22, 5, false,
// 2,
// "A_LAISE"));

// List<PersonResponse> betaTeam = Arrays.asList(
// new PersonResponse(UUID.randomUUID(), "Lucas Hernandez", "MALE", 26, 2,
// false, 3,
// "TIMIDE"),
// new PersonResponse(UUID.randomUUID(), "Sophie Laurance", "FEMALE", 30, 4,
// true, 4,
// "A_LAISE"),
// new PersonResponse(UUID.randomUUID(), "Julien Robert", "MALE", 25, 3, false,
// 2,
// "RESERVE"));

// List<PersonResponse> gammaTeam = Arrays.asList(
// new PersonResponse(UUID.randomUUID(), "Gal Gadot", "FEMALE", 27, 3, false, 4,
// "A_LAISE"),
// new PersonResponse(UUID.randomUUID(), "Antoine Grizman", "MALE", 23, 4,
// false, 3,
// "RESERVE"),
// new PersonResponse(UUID.randomUUID(), "Pique Girard", "FEMALE", 29, 2, true,
// 3,
// "TIMIDE"));

// List<GroupResponse> savedGroups = Arrays.asList(
// GroupResponse.builder()
// .id(UUID.randomUUID())
// .name("Groupe A")
// .drawId(drawId)
// .persons(alphaTeam)
// .personCount(alphaTeam.size())
// .build(),
// GroupResponse.builder()
// .id(UUID.randomUUID())
// .name("Groupe B")
// .drawId(drawId)
// .persons(betaTeam)
// .personCount(betaTeam.size())
// .build(),
// GroupResponse.builder()
// .id(UUID.randomUUID())
// .name("Groupe C")
// .drawId(drawId)
// .persons(gammaTeam)
// .personCount(gammaTeam.size())
// .build());

// return DrawResponse.builder()
// .id(drawId)
// .title("Promo CDA - Mai 2026")
// .createdAt(LocalDateTime.now())
// .listId(listId)
// .listName("Promo CDA - Mai 2026")
// .groupCount(3)
// .groups(savedGroups)
// .build();
// }

// private void displayPreviewResults(MvcResult result) throws Exception {
// String responseBody = result.getResponse().getContentAsString();
// GroupPreviewResponse response = objectMapper.readValue(responseBody,
// GroupPreviewResponse.class);

// System.out.println("\nPREVIEW RESPONSE:");
// System.out.println("List: " + response.getListName());
// System.out.println("Title: " + response.getTitle());
// System.out.println("Total Persons: " + response.getTotalPersons());
// System.out.println("Groups Count: " + response.getGroupCount());
// System.out.println("Generated At: " + response.getGeneratedAt());

// System.out.println("\n👥 PREVIEW GROUPS (No Database IDs):");
// for (GroupResponse group : response.getGroups()) {
// System.out.println("\n" + group.getName() + " (" + group.getPersonCount() + "
// persons)");
// System.out.println(
// " Group ID: " + (group.getId() != null ? group.getId() : "NULL (preview)"));
// System.out.println(" Draw ID: "
// + (group.getDrawId() != null ? group.getDrawId() : "NULL (preview)"));

// for (PersonResponse person : group.getPersons()) {
// System.out.println(" 👤 " + person.getName() +
// " (" + person.getGender() + ", " + person.getAge() + " ans)" +
// " - FR:" + person.getFrenchLevel() + "/5" +
// " - Tech:" + person.getTechLevel() + "/5" +
// " - " + person.getProfile() +
// (person.getOldDwwm() ? " - Ex-DWWM" : ""));
// }
// }
// }

// private void displaySaveResults(MvcResult result) throws Exception {
// String responseBody = result.getResponse().getContentAsString();
// DrawResponse response = objectMapper.readValue(responseBody,
// DrawResponse.class);

// System.out.println("\n SAVE RESPONSE:");
// System.out.println("Draw ID: " + response.getId());
// System.out.println("Title: " + response.getTitle());
// System.out.println("Created At: " + response.getCreatedAt());
// System.out.println("List: " + response.getListName());
// System.out.println("Groups Saved: " + response.getGroupCount());

// System.out.println("\n SAVED GROUPS (With Database IDs):");
// for (GroupResponse group : response.getGroups()) {
// System.out.println("\n " + group.getName() + " (" + group.getPersonCount() +
// " persons)");
// System.out.println(" Group ID: " + group.getId());
// System.out.println(" Draw ID: " + group.getDrawId());

// for (PersonResponse person : group.getPersons()) {
// System.out.println(" 👤 " + person.getName() +
// " (" + person.getGender() + ", " + person.getAge() + " ans)" +
// " - FR:" + person.getFrenchLevel() + "/5" +
// " - Tech:" + person.getTechLevel() + "/5" +
// " - " + person.getProfile() +
// (person.getOldDwwm() ? " - Ex-DWWM" : ""));
// }
// }
// }

// // private void displayGroupsResults(MvcResult result, UUID drawId) throws
// // Exception {
// // String responseBody = result.getResponse().getContentAsString();
// // GroupResponse[] groups = objectMapper.readValue(responseBody,
// // GroupResponse[].class);

// // System.out.println("\nGET GROUPS RESPONSE:");
// // System.out.println("Draw ID: " + drawId);
// // System.out.println("Retrieved " + groups.length + " groups from
// database:");

// // for (GroupResponse group : groups) {
// // System.out.println("\n" + group.getName() + " (" + group.getPersonCount()
// + "
// // persons)");
// // System.out.println(" Group ID: " + group.getId());
// // System.out.println(" Draw ID: " + group.getDrawId());

// // for (PersonResponse person : group.getPersons()) {
// // System.out.println(" " + person.getName() +
// // " (" + person.getGender() + ", " + person.getAge() + " ans)" +
// // " - FR:" + person.getFrenchLevel() + "/5" +
// // " - Tech:" + person.getTechLevel() + "/5" +
// // " - " + person.getProfile() +
// // (person.getOldDwwm() ? " - Ex-DWWM" : ""));
// // }
// // }
// // }
// }