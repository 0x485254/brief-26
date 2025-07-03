package com.easygroup.controller;

import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.dto.GroupResponse;
import com.easygroup.entity.Draw;
import com.easygroup.entity.Group;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.Person;
import com.easygroup.repository.GroupRepository;
import com.easygroup.repository.PersonRepository;
import com.easygroup.service.GroupGenerationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupGenerationServiceTests {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private GroupGenerationService groupGenerationService;

    private ListEntity list;
    private Draw draw;
    private List<Person> persons;
    private GenerateGroupsRequest request;

    @BeforeEach
    void setup() {
        list = new ListEntity();
        list.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        list.setName("Hollywood Actors");

        draw = new Draw();
        draw.setId(UUID.fromString("00000000-0000-0000-0000-000000000010"));
        draw.setTitle("Oscars Draw");
        draw.setList(list);

        persons = Arrays.asList(
                createPerson(UUID.fromString("00000000-0000-0000-0000-000000000011"), "Tom Hanks", Person.Gender.MALE,
                        5, 3, true, Person.Profile.A_LAISE, 64),
                createPerson(UUID.fromString("00000000-0000-0000-0000-000000000012"), "Meryl Streep",
                        Person.Gender.FEMALE, 6, 4, false, Person.Profile.RESERVE, 71),
                createPerson(UUID.fromString("00000000-0000-0000-0000-000000000013"), "Denzel Washington",
                        Person.Gender.MALE, 4, 3, true, Person.Profile.A_LAISE, 66),
                createPerson(UUID.fromString("00000000-0000-0000-0000-000000000014"), "Scarlett Johansson",
                        Person.Gender.FEMALE, 5, 4, false, Person.Profile.TIMIDE, 36),
                createPerson(UUID.fromString("00000000-0000-0000-0000-000000000015"), "Morgan Freeman",
                        Person.Gender.MALE, 6, 3, true, Person.Profile.RESERVE, 83));

        request = new GenerateGroupsRequest();
        request.setNumberOfGroups(2);
        request.setGroupNames(Arrays.asList("Group A", "Group B"));
        request.setBalanceByGender(true);
        request.setBalanceByTechLevel(true);
        request.setBalanceByFrenchLevel(true);
        request.setBalanceByOldDwwm(true);
        request.setBalanceByProfile(true);
        request.setBalanceByAge(true);
    }

    @Test
    void generateGroups_shouldThrow_whenNoPersons() {
        when(personRepository.findByList(draw.getList())).thenReturn(Collections.emptyList());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> groupGenerationService.generateGroups(draw, request));

        assertTrue(ex.getMessage().contains("No persons found"));
        verifyNoInteractions(groupRepository);
    }

    @Test
    void generateGroups_shouldThrow_whenNotEnoughPersonsForGroups() {
        when(personRepository.findByList(draw.getList())).thenReturn(persons.subList(0, 1)); // only 1 person
        request.setNumberOfGroups(3); // 3 groups requested

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> groupGenerationService.generateGroups(draw, request));

        assertTrue(ex.getMessage().contains("Not enough persons"));
        verifyNoInteractions(groupRepository);
    }

    @Test
    void generateGroups_shouldCreateAndDistributeGroupsProperly() {
        when(personRepository.findByList(draw.getList())).thenReturn(persons);

        groupGenerationService.generateGroups(draw, request);

        ArgumentCaptor<List<Group>> captor = ArgumentCaptor.forClass(List.class);
        verify(groupRepository).saveAll(captor.capture());

        List<Group> savedGroups = captor.getValue();
        assertEquals(request.getNumberOfGroups(), savedGroups.size());

        int totalPersons = savedGroups.stream()
                .mapToInt(g -> g.getPersons().size())
                .sum();

        assertEquals(persons.size(), totalPersons);

        for (Group g : savedGroups) {
            assertTrue(request.getGroupNames().contains(g.getName()));
            assertNotNull(g.getDraw());
            assertFalse(g.getPersons().isEmpty());
        }
    }

    @Test
    void generateGroupsPreview_shouldThrow_whenNoPersons() {
        when(personRepository.findByList(list)).thenReturn(Collections.emptyList());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> groupGenerationService.generateGroupsPreview(list, request));

        assertTrue(ex.getMessage().contains("No persons found"));
    }

    @Test
    void generateGroupsPreview_shouldThrow_whenNotEnoughPersons() {
        when(personRepository.findByList(list)).thenReturn(persons.subList(0, 1));
        request.setNumberOfGroups(3);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> groupGenerationService.generateGroupsPreview(list, request));

        assertTrue(ex.getMessage().contains("Not enough persons"));
    }

    @Test
    void generateGroupsPreview_shouldReturnGroupResponses() {
        when(personRepository.findByList(list)).thenReturn(persons);

        List<GroupResponse> preview = groupGenerationService.generateGroupsPreview(list, request);

        assertEquals(request.getNumberOfGroups(), preview.size());
        for (GroupResponse gr : preview) {
            assertNotNull(gr.getName());
            assertNotNull(gr.getPersons());
        }
    }

    @Test
    void savePreviewToDatabase_shouldSaveGroupsAndPersons() {
        Draw savedDraw = new Draw();
        savedDraw.setId(UUID.fromString("00000000-0000-0000-0000-000000000020"));
        savedDraw.setTitle("Saved Draw");

        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setName("Group A");
        groupResponse.setPersons(persons.stream()
                .map(p -> {
                    var pr = new com.easygroup.dto.PersonResponse();
                    pr.setPersonId(p.getId());
                    return pr;
                }).collect(Collectors.toList()));

        List<GroupResponse> preview = Collections.singletonList(groupResponse);

        when(personRepository.findById(any())).thenAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            return persons.stream().filter(p -> p.getId().equals(id)).findFirst();
        });

        groupGenerationService.savePreviewToDatabase(savedDraw, preview);

        ArgumentCaptor<List<Group>> captor = ArgumentCaptor.forClass(List.class);
        verify(groupRepository).saveAll(captor.capture());

        List<Group> savedGroups = captor.getValue();
        assertEquals(1, savedGroups.size());
        assertEquals("Group A", savedGroups.get(0).getName());
        assertEquals(persons.size(), savedGroups.get(0).getPersons().size());
        assertEquals(savedDraw, savedGroups.get(0).getDraw());
    }

    @Test
    void savePreviewToDatabase_shouldThrow_whenPersonNotFound() {
        Draw savedDraw = new Draw();
        savedDraw.setId(UUID.fromString("00000000-0000-0000-0000-000000000020"));
        savedDraw.setTitle("Saved Draw");

        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setName("Group A");
        com.easygroup.dto.PersonResponse pr = new com.easygroup.dto.PersonResponse();
        pr.setPersonId(UUID.fromString("00000000-0000-0000-0000-000000099999")); // Not in persons
        groupResponse.setPersons(Collections.singletonList(pr));

        List<GroupResponse> preview = Collections.singletonList(groupResponse);

        when(personRepository.findById(any())).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> groupGenerationService.savePreviewToDatabase(savedDraw, preview));

        assertTrue(ex.getMessage().contains("Person not found"));
    }

    private Person createPerson(UUID id, String name, Person.Gender gender, int techLevel, int frenchLevel,
            boolean oldDwwm, Person.Profile profile, int age) {
        Person p = new Person();
        p.setId(id);
        p.setName(name);
        p.setGender(gender);
        p.setTechLevel(techLevel);
        p.setFrenchLevel(frenchLevel);
        p.setOldDwwm(oldDwwm);
        p.setProfile(profile);
        p.setAge(age);
        return p;
    }

}
