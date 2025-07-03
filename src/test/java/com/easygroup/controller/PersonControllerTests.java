package com.easygroup.controller;

import com.easygroup.dto.PersonRequest;
import com.easygroup.dto.PersonResponse;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.Person;
import com.easygroup.entity.User;
import com.easygroup.mapper.PersonMapper;
import com.easygroup.service.ListService;
import com.easygroup.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonControllerTests {

    @Mock
    private PersonService personService;

    @Mock
    private ListService listService;

    @InjectMocks
    private PersonController personController;

    private User testUser;
    private ListEntity testList;
    private Person testPerson1;
    private Person testPerson2;
    private PersonRequest personRequest;
    private PersonResponse personResponse;
    private UUID userId;
    private UUID listId;
    private UUID personId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        listId = UUID.randomUUID();
        personId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("user@test.com");

        testList = new ListEntity();
        testList.setId(listId);
        testList.setName("Test List");
        testList.setUser(testUser);

        testPerson1 = new Person();
        testPerson1.setId(UUID.randomUUID());
        testPerson1.setName("Dodo Dada");
        testPerson1.setGender(Person.Gender.MALE);
        testPerson1.setAge(25);
        testPerson1.setFrenchLevel(3);
        testPerson1.setOldDwwm(false);
        testPerson1.setTechLevel(2);
        testPerson1.setProfile(Person.Profile.A_LAISE);
        testPerson1.setList(testList);

        testPerson2 = new Person();
        testPerson2.setId(UUID.randomUUID());
        testPerson2.setName("Lulu Nono");
        testPerson2.setGender(Person.Gender.FEMALE);
        testPerson2.setAge(30);
        testPerson2.setFrenchLevel(4);
        testPerson2.setOldDwwm(true);
        testPerson2.setTechLevel(3);
        testPerson2.setProfile(Person.Profile.RESERVE);
        testPerson2.setList(testList);

        personRequest = new PersonRequest();
        personRequest.setName("New Person");
        personRequest.setGender(Person.Gender.OTHER);
        personRequest.setAge(28);
        personRequest.setFrenchLevel(3);
        personRequest.setOldDwwm(false);
        personRequest.setTechLevel(2);
        personRequest.setProfile(Person.Profile.TIMIDE);

        personResponse = new PersonResponse();
        personResponse.setPersonId(personId);
        personResponse.setName("New Person");
        personResponse.setGender("OTHER");
        personResponse.setAge(28);
        personResponse.setFrenchLevel(3);
        personResponse.setOldDwwm(false);
        personResponse.setTechLevel(2);
        personResponse.setProfile("TIMIDE");
    }

    @Test
    void getPersons_Success() {
        List<Person> persons = Arrays.asList(testPerson1, testPerson2);
        when(listService.findByIdAndUserId(listId, userId)).thenReturn(testList);
        when(personService.findByList(testList)).thenReturn(persons);

        try (MockedStatic<PersonMapper> mockedMapper = mockStatic(PersonMapper.class)) {
            PersonResponse response1 = new PersonResponse();
            response1.setPersonId(testPerson1.getId());
            response1.setName("Dodo Dada");
            response1.setGender("MALE");

            PersonResponse response2 = new PersonResponse();
            response2.setPersonId(testPerson2.getId());
            response2.setName("Lulu Nono");
            response2.setGender("FEMALE");

            mockedMapper.when(() -> PersonMapper.toDto(testPerson1)).thenReturn(response1);
            mockedMapper.when(() -> PersonMapper.toDto(testPerson2)).thenReturn(response2);

            ResponseEntity<List<PersonResponse>> result = personController.getPersons(listId, testUser);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertNotNull(result.getBody());
            assertEquals(2, result.getBody().size());
            assertEquals("Dodo Dada", result.getBody().get(0).getName());
            assertEquals("Lulu Nono", result.getBody().get(1).getName());
        }
    }

    @Test
    void getPersons_ListNotOwned_ReturnsForbidden() {
        when(listService.findByIdAndUserId(listId, userId)).thenReturn(null);

        ResponseEntity<List<PersonResponse>> result = personController.getPersons(listId, testUser);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertNull(result.getBody());
        verify(personService, never()).findByList(any());
    }

    @Test
    void getPersons_EmptyList_Success() {
        when(listService.findByIdAndUserId(listId, userId)).thenReturn(testList);
        when(personService.findByList(testList)).thenReturn(Arrays.asList());

        try (MockedStatic<PersonMapper> mockedMapper = mockStatic(PersonMapper.class)) {
            ResponseEntity<List<PersonResponse>> result = personController.getPersons(listId, testUser);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertNotNull(result.getBody());
            assertTrue(result.getBody().isEmpty());
        }
    }

    @Test
    void createPerson_Success() {
        Person editedPerson = new Person();
        editedPerson.setId(personId);
        editedPerson.setName("New Person");
        editedPerson.setGender(Person.Gender.OTHER);

        when(personService.edit(any(Person.class))).thenReturn(editedPerson);

        try (MockedStatic<PersonMapper> mockedMapper = mockStatic(PersonMapper.class)) {
            mockedMapper.when(() -> PersonMapper.toDto(editedPerson)).thenReturn(personResponse);

            ResponseEntity<PersonResponse> result = personController.createPerson(personRequest, testUser, personId);

            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertNotNull(result.getBody());
            assertEquals(personId, result.getBody().getPersonId());
            assertEquals("New Person", result.getBody().getName());
            assertEquals("OTHER", result.getBody().getGender());
        }
    }

    @Test
    void updatePerson_Success() {
        Person savedPerson = new Person();
        savedPerson.setId(UUID.randomUUID());
        savedPerson.setName("New Person");
        savedPerson.setList(testList);

        when(listService.findByIdAndUserId(listId, userId)).thenReturn(testList);
        when(personService.save(any(Person.class))).thenReturn(savedPerson);

        try (MockedStatic<PersonMapper> mockedMapper = mockStatic(PersonMapper.class)) {
            mockedMapper.when(() -> PersonMapper.toDto(savedPerson)).thenReturn(personResponse);

            ResponseEntity<PersonResponse> result = personController.updatePerson(listId, personRequest, testUser);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertNotNull(result.getBody());
            assertEquals("New Person", result.getBody().getName());
            verify(personService).save(any(Person.class));
        }
    }

    @Test
    void updatePerson_ListNotOwned_ReturnsForbidden() {
        when(listService.findByIdAndUserId(listId, userId)).thenReturn(null);

        ResponseEntity<PersonResponse> result = personController.updatePerson(listId, personRequest, testUser);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertNull(result.getBody());
        verify(personService, never()).save(any());
    }

    @Test
    void deletePerson_Success() {
        when(listService.findByIdAndUserId(listId, userId)).thenReturn(testList);
        ResponseEntity<Void> result = personController.deletePerson(listId, personId, testUser);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(personService).deleteById(personId);
    }

    @Test
    void deletePerson_ListNotOwned_ReturnsForbidden() {
        when(listService.findByIdAndUserId(listId, userId)).thenReturn(null);

        ResponseEntity<Void> result = personController.deletePerson(listId, personId, testUser);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        verify(personService, never()).deleteById(any());
    }

    @Test
    void deletePerson_PersonNotFound_StillSucceeds() {
        when(listService.findByIdAndUserId(listId, userId)).thenReturn(testList);
        doNothing().when(personService).deleteById(personId);

        ResponseEntity<Void> result = personController.deletePerson(listId, personId, testUser);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(personService).deleteById(personId);
    }
}