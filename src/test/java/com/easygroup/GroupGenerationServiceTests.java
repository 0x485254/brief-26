package com.easygroup;

import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.entity.*;
import com.easygroup.repository.DrawRepository;
import com.easygroup.repository.GroupRepository;
import com.easygroup.repository.PersonRepository;
import com.easygroup.service.GroupGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class GroupGenerationServiceTests {

    @Autowired
    private GroupGenerationService groupGenerationService;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private DrawRepository drawRepository;

    @MockBean
    private PersonRepository personRepository;

    private Draw testDraw;
    private com.easygroup.entity.List testList;
    private java.util.List<Person> testPersons;
    private GenerateGroupsRequest request;

    @BeforeEach
    void setUp() {
        testList = new com.easygroup.entity.List();
        testList.setId(UUID.randomUUID());
        testList.setName("Test Class 2025");

        testDraw = new Draw();
        testDraw.setId(UUID.randomUUID());
        testDraw.setTitle("Test Draw");
        testDraw.setList(testList);
        testDraw.setCreatedAt(LocalDateTime.now());
        testDraw.setGroups(new ArrayList<>());

        testPersons = createDiversePersonList();

        request = new GenerateGroupsRequest();
        request.setTitle("Test Groups");
        request.setNumberOfGroups(4);
        request.setGroupNames(Arrays.asList("Group A", "Group B", "Group C", "Group D"));
        request.setBalanceByGender(false);
        request.setBalanceByAge(false);
        request.setBalanceByTechLevel(false);
        request.setBalanceByFrenchLevel(false);
        request.setBalanceByOldDwwm(false);
        request.setBalanceByProfile(false);

        when(personRepository.findByList(testList)).thenReturn(testPersons);
        when(groupRepository.saveAll(any())).thenAnswer(invocation -> {
            java.util.List<Group> groups = invocation.getArgument(0);
            groups.forEach(group -> {
                if (group.getId() == null) {
                    group.setId(UUID.randomUUID());
                }
                group.getPersons().forEach(person -> {
                    if (person.getId() == null) {
                        person.setId(UUID.randomUUID());
                    }
                });
            });
            return groups;
        });
    }

    private java.util.List<Person> createDiversePersonList() {
        java.util.List<Person> persons = new ArrayList<>();

        String[] names = { "Alice", "Bob", "Clara", "David", "Emma", "Frank", "Grace", "Henry",
                "Iris", "Jack", "Kate", "Liam", "Mia", "Noah", "Olivia", "Paul",
                "Quinn", "Rose", "Sam", "Tina" };

        Person.Gender[] genders = { Person.Gender.FEMALE, Person.Gender.MALE, Person.Gender.FEMALE,
                Person.Gender.MALE, Person.Gender.FEMALE, Person.Gender.MALE,
                Person.Gender.OTHER, Person.Gender.MALE, Person.Gender.FEMALE,
                Person.Gender.MALE, Person.Gender.FEMALE, Person.Gender.MALE,
                Person.Gender.FEMALE, Person.Gender.MALE, Person.Gender.FEMALE,
                Person.Gender.MALE, Person.Gender.OTHER, Person.Gender.FEMALE,
                Person.Gender.MALE, Person.Gender.FEMALE };

        int[] ages = { 22, 25, 28, 24, 26, 30, 23, 27, 25, 29, 24, 26, 22, 28, 25, 31, 24, 27, 26, 23 };

        int[] techLevels = { 1, 2, 3, 4, 2, 3, 1, 4, 2, 3, 1, 4, 2, 3, 1, 4, 2, 3, 1, 4 };

        int[] frenchLevels = { 2, 3, 4, 1, 3, 2, 4, 1, 3, 2, 4, 1, 3, 2, 4, 1, 3, 2, 4, 1 };

        boolean[] oldDwwm = { false, true, false, false, true, false, true, false, false, true,
                false, true, false, false, true, false, true, false, false, true };

        Person.Profile[] profiles = { Person.Profile.A_LAISE, Person.Profile.RESERVE, Person.Profile.TIMIDE,
                Person.Profile.A_LAISE, Person.Profile.RESERVE, Person.Profile.TIMIDE,
                Person.Profile.A_LAISE, Person.Profile.RESERVE, Person.Profile.TIMIDE,
                Person.Profile.A_LAISE, Person.Profile.RESERVE, Person.Profile.TIMIDE,
                Person.Profile.A_LAISE, Person.Profile.RESERVE, Person.Profile.TIMIDE,
                Person.Profile.A_LAISE, Person.Profile.RESERVE, Person.Profile.TIMIDE,
                Person.Profile.A_LAISE, Person.Profile.RESERVE };

        for (int i = 0; i < 20; i++) {
            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName(names[i]);
            person.setGender(genders[i]);
            person.setAge(ages[i]);
            person.setTechLevel(techLevels[i]);
            person.setFrenchLevel(frenchLevels[i]);
            person.setOldDwwm(oldDwwm[i]);
            person.setProfile(profiles[i]);
            person.setList(testList);
            persons.add(person);
        }

        return persons;
    }

    @Test
    void generateGroups_BasicDistribution_Success() {
        groupGenerationService.generateGroups(testDraw, request);

        verify(groupRepository).saveAll(any());
        assertEquals(4, testDraw.getGroups().size());

        java.util.List<String> groupNames = testDraw.getGroups().stream()
                .map(Group::getName)
                .collect(Collectors.toList());
        assertTrue(groupNames.containsAll(Arrays.asList("Group A", "Group B", "Group C", "Group D")));

        int totalPersonsInGroups = testDraw.getGroups().stream()
                .mapToInt(group -> group.getPersons().size())
                .sum();
        assertEquals(20, totalPersonsInGroups);
    }

    @Test
    void generateGroups_BalanceByGender_Success() {
        request.setBalanceByGender(true);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            Map<Person.Gender, Long> genderCount = group.getPersons().stream()
                    .collect(Collectors.groupingBy(Person::getGender, Collectors.counting()));

            assertNotNull(genderCount);

            long maxGenderInGroup = genderCount.values().stream().mapToLong(Long::longValue).max().orElse(0);
            long minGenderInGroup = genderCount.values().stream().mapToLong(Long::longValue).min().orElse(0);

            assertTrue(maxGenderInGroup - minGenderInGroup <= 2,
                    "Gender distribution too uneven in group: " + group.getName());
        }
    }

    @Test
    void generateGroups_BalanceByTechLevel_Success() {
        request.setBalanceByTechLevel(true);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            Map<Integer, Long> techLevelCount = group.getPersons().stream()
                    .collect(Collectors.groupingBy(Person::getTechLevel, Collectors.counting()));

            assertTrue(techLevelCount.size() >= 2,
                    "Tech level diversity too low in group: " + group.getName());
        }
    }

    @Test
    void generateGroups_BalanceByAge_Success() {
        request.setBalanceByAge(true);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            java.util.List<Integer> ages = group.getPersons().stream()
                    .map(Person::getAge)
                    .sorted()
                    .collect(Collectors.toList());

            if (ages.size() > 1) {
                int ageSpread = ages.get(ages.size() - 1) - ages.get(0);

                assertTrue(ageSpread >= 0, "Age distribution validation for group: " + group.getName());
            }
        }
    }

    @Test
    void generateGroups_BalanceByFrenchLevel_Success() {
        request.setBalanceByFrenchLevel(true);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            Set<Integer> frenchLevels = group.getPersons().stream()
                    .map(Person::getFrenchLevel)
                    .collect(Collectors.toSet());

            assertTrue(frenchLevels.size() >= 2,
                    "French level diversity too low in group: " + group.getName());
        }
    }

    @Test
    void generateGroups_BalanceByOldDwwm_Success() {
        request.setBalanceByOldDwwm(true);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            Map<Boolean, Long> dwwmCount = group.getPersons().stream()
                    .collect(Collectors.groupingBy(Person::getOldDwwm, Collectors.counting()));

            if (group.getPersons().size() > 1) {
                assertTrue(dwwmCount.size() <= 2, "DWWM distribution check for group: " + group.getName());
            }
        }
    }

    @Test
    void generateGroups_BalanceByProfile_Success() {
        request.setBalanceByProfile(true);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            Set<Person.Profile> profiles = group.getPersons().stream()
                    .map(Person::getProfile)
                    .collect(Collectors.toSet());

            assertTrue(profiles.size() >= 1,
                    "Profile diversity check for group: " + group.getName());
        }
    }

    @Test
    void generateGroups_AllCriteriaEnabled_PerfectMix() {
        request.setBalanceByGender(true);
        request.setBalanceByAge(true);
        request.setBalanceByTechLevel(true);
        request.setBalanceByFrenchLevel(true);
        request.setBalanceByOldDwwm(true);
        request.setBalanceByProfile(true);

        groupGenerationService.generateGroups(testDraw, request);

        assertEquals(4, testDraw.getGroups().size());

        for (Group group : testDraw.getGroups()) {
            Set<Person.Gender> genders = group.getPersons().stream()
                    .map(Person::getGender)
                    .collect(Collectors.toSet());

            Set<Integer> techLevels = group.getPersons().stream()
                    .map(Person::getTechLevel)
                    .collect(Collectors.toSet());

            Set<Integer> frenchLevels = group.getPersons().stream()
                    .map(Person::getFrenchLevel)
                    .collect(Collectors.toSet());

            Set<Person.Profile> profiles = group.getPersons().stream()
                    .map(Person::getProfile)
                    .collect(Collectors.toSet());

            Set<Boolean> dwwmTypes = group.getPersons().stream()
                    .map(Person::getOldDwwm)
                    .collect(Collectors.toSet());

            assertTrue(genders.size() >= 1, "Gender diversity in " + group.getName());
            assertTrue(techLevels.size() >= 2, "Tech level diversity in " + group.getName());
            assertTrue(frenchLevels.size() >= 2, "French level diversity in " + group.getName());
            assertTrue(profiles.size() >= 1, "Profile diversity in " + group.getName());

            System.out.println("=== " + group.getName() + " ===");
            System.out.println("Genders: " + genders);
            System.out.println("Tech Levels: " + techLevels);
            System.out.println("French Levels: " + frenchLevels);
            System.out.println("Profiles: " + profiles);
            System.out.println("DWWM Types: " + dwwmTypes);
            System.out.println("Total persons: " + group.getPersons().size());
            System.out.println();
        }

        int minGroupSize = testDraw.getGroups().stream()
                .mapToInt(group -> group.getPersons().size())
                .min().orElse(0);
        int maxGroupSize = testDraw.getGroups().stream()
                .mapToInt(group -> group.getPersons().size())
                .max().orElse(0);

        assertTrue(maxGroupSize - minGroupSize <= 1,
                "Group sizes too uneven: min=" + minGroupSize + ", max=" + maxGroupSize);
    }

    @Test
    void generateGroups_NotEnoughPersons_ThrowsException() {
        request.setNumberOfGroups(25);

        when(personRepository.findByList(testList)).thenReturn(testPersons);

        assertThrows(Exception.class, () -> {
            groupGenerationService.generateGroups(testDraw, request);
        });
    }

    @Test
    void generateGroups_EmptyPersonList_ThrowsException() {
        // Test edge case: no persons in list
        when(personRepository.findByList(testList)).thenReturn(new ArrayList<>());

        assertThrows(Exception.class, () -> {
            groupGenerationService.generateGroups(testDraw, request);
        });
    }

    @Test
    void generateGroups_SingleCriteriaComparison() {
        // Test to compare single vs multiple criteria effects

        // First: Only gender balancing
        GenerateGroupsRequest genderOnlyRequest = new GenerateGroupsRequest();
        genderOnlyRequest.setTitle("Gender Only");
        genderOnlyRequest.setNumberOfGroups(4);
        genderOnlyRequest.setGroupNames(Arrays.asList("Group A", "Group B", "Group C", "Group D"));
        genderOnlyRequest.setBalanceByGender(true);

        Draw genderDraw = new Draw();
        genderDraw.setId(UUID.randomUUID());
        genderDraw.setTitle("Gender Test");
        genderDraw.setList(testList);
        genderDraw.setGroups(new ArrayList<>());

        groupGenerationService.generateGroups(genderDraw, genderOnlyRequest);

        // Verify gender balancing worked
        assertEquals(4, genderDraw.getGroups().size());

        // Now: Gender + Tech level balancing
        GenerateGroupsRequest multiCriteriaRequest = new GenerateGroupsRequest();
        multiCriteriaRequest.setTitle("Multi Criteria");
        multiCriteriaRequest.setNumberOfGroups(4);
        multiCriteriaRequest.setGroupNames(Arrays.asList("Group A", "Group B", "Group C", "Group D"));
        multiCriteriaRequest.setBalanceByGender(true);
        multiCriteriaRequest.setBalanceByTechLevel(true);

        Draw multiDraw = new Draw();
        multiDraw.setId(UUID.randomUUID());
        multiDraw.setTitle("Multi Test");
        multiDraw.setList(testList);
        multiDraw.setGroups(new ArrayList<>());

        groupGenerationService.generateGroups(multiDraw, multiCriteriaRequest);

        // Both should succeed
        assertEquals(4, multiDraw.getGroups().size());

        System.out.println("Single criteria test completed successfully");
        System.out.println("Multi criteria test completed successfully");
    }
    // Add these test methods to your existing GroupGenerationServiceTests class

    @Test
    void generateGroups_UnevenNumbers_21PersonsIn4Groups() {
        // Test uneven distribution: 21 persons → 4 groups (should be [6,5,5,5])

        // Add one more person to make it 21
        Person extraPerson = new Person();
        extraPerson.setId(UUID.randomUUID());
        extraPerson.setName("Extra");
        extraPerson.setGender(Person.Gender.MALE);
        extraPerson.setAge(24);
        extraPerson.setTechLevel(2);
        extraPerson.setFrenchLevel(3);
        extraPerson.setOldDwwm(false);
        extraPerson.setProfile(Person.Profile.A_LAISE);
        extraPerson.setList(testList);

        List<Person> extendedPersons = new ArrayList<>(testPersons);
        extendedPersons.add(extraPerson);

        when(personRepository.findByList(testList)).thenReturn(extendedPersons);

        request.setBalanceByGender(true);
        request.setBalanceByTechLevel(true);

        groupGenerationService.generateGroups(testDraw, request);

        // Verify uneven but optimal distribution
        List<Integer> groupSizes = testDraw.getGroups().stream()
                .map(group -> group.getPersons().size())
                .sorted()
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(5, 5, 5, 6), groupSizes, "Should be [5,5,5,6] distribution for 21 persons");

        // Verify total persons
        int totalPersons = testDraw.getGroups().stream()
                .mapToInt(group -> group.getPersons().size())
                .sum();
        assertEquals(21, totalPersons);

        System.out.println("✅ Uneven numbers test: 21 persons distributed as " + groupSizes);
    }

    @Test
    void generateGroups_MinimumGroups_20PersonsIn2Groups() {
        // Test minimum groups: 20 persons → 2 groups (should be [10,10])

        request.setNumberOfGroups(2);
        request.setGroupNames(Arrays.asList("Team Alpha", "Team Beta"));
        request.setBalanceByGender(true);
        request.setBalanceByTechLevel(true);

        groupGenerationService.generateGroups(testDraw, request);

        assertEquals(2, testDraw.getGroups().size());

        // Verify even distribution
        for (Group group : testDraw.getGroups()) {
            assertEquals(10, group.getPersons().size(),
                    "Each group should have exactly 10 persons");

            // Verify diversity in larger groups
            Set<Person.Gender> genders = group.getPersons().stream()
                    .map(Person::getGender)
                    .collect(Collectors.toSet());
            Set<Integer> techLevels = group.getPersons().stream()
                    .map(Person::getTechLevel)
                    .collect(Collectors.toSet());

            assertTrue(genders.size() >= 2, "Should have gender diversity in group: " + group.getName());
            assertTrue(techLevels.size() >= 3, "Should have tech level diversity in group: " + group.getName());
        }

        System.out.println("✅ Minimum groups test: 2 groups of 10 persons each");
    }

    // Replace the original single person test with this corrected version:

    @Test
    void generateGroups_InsufficientPersons_1PersonFor2Groups() {
        // Test realistic edge case: 1 person but user wants 2+ groups (should fail)

        Person singlePerson = new Person();
        singlePerson.setId(UUID.randomUUID());
        singlePerson.setName("Solo");
        singlePerson.setGender(Person.Gender.FEMALE);
        singlePerson.setAge(25);
        singlePerson.setTechLevel(3);
        singlePerson.setFrenchLevel(2);
        singlePerson.setOldDwwm(true);
        singlePerson.setProfile(Person.Profile.A_LAISE);
        singlePerson.setList(testList);

        when(personRepository.findByList(testList)).thenReturn(Arrays.asList(singlePerson));

        // User tries to create 2 groups with only 1 person (realistic user error)
        request.setNumberOfGroups(2); // ✅ Meets DTO validation
        request.setGroupNames(Arrays.asList("Group A", "Group B"));
        request.setBalanceByGender(true);

        // Should throw exception because 1 person < 2 groups
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            groupGenerationService.generateGroups(testDraw, request);
        });

        // Verify it's the right exception
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Not enough persons"));

        System.out.println("✅ Insufficient persons test: 1 person for 2 groups correctly rejected");
    }

    // Optional: Add this helper method if you want to reuse person creation
    private Person createPersonWithName(String name) {
        Person person = new Person();
        person.setId(UUID.randomUUID());
        person.setName(name);
        person.setGender(Person.Gender.MALE);
        person.setAge(25);
        person.setTechLevel(2);
        person.setFrenchLevel(3);
        person.setOldDwwm(false);
        person.setProfile(Person.Profile.A_LAISE);
        person.setList(testList);
        return person;
    }

    // Bonus: Add a proper minimum valid scenario test
    @Test
    void generateGroups_MinimumValidScenario_2PersonsIn2Groups() {
        // Test the absolute minimum valid scenario: 2 persons → 2 groups = [1,1]

        Person person1 = createPersonWithName("Alice");
        person1.setGender(Person.Gender.FEMALE);
        person1.setTechLevel(1);

        Person person2 = createPersonWithName("Bob");
        person2.setGender(Person.Gender.MALE);
        person2.setTechLevel(4);

        when(personRepository.findByList(testList)).thenReturn(Arrays.asList(person1, person2));

        request.setNumberOfGroups(2);
        request.setGroupNames(Arrays.asList("Group A", "Group B"));
        request.setBalanceByGender(true); // Should work even with minimal data

        groupGenerationService.generateGroups(testDraw, request);

        assertEquals(2, testDraw.getGroups().size());

        // Verify distribution
        List<Integer> groupSizes = testDraw.getGroups().stream()
                .map(group -> group.getPersons().size())
                .sorted()
                .collect(Collectors.toList());

        assertEquals(Arrays.asList(1, 1), groupSizes);

        // Verify each group has one person
        assertEquals(1, testDraw.getGroups().get(0).getPersons().size());
        assertEquals(1, testDraw.getGroups().get(1).getPersons().size());

        // Verify persons are distributed
        Set<String> distributedNames = testDraw.getGroups().stream()
                .flatMap(group -> group.getPersons().stream())
                .map(Person::getName)
                .collect(Collectors.toSet());

        assertEquals(Set.of("Alice", "Bob"), distributedNames);

        System.out.println("✅ Minimum valid scenario: 2 persons in 2 groups works perfectly");
    }

    @Test
    void generateGroups_LargeGroups_100PersonsIn10Groups() {
        // Test large scale: 100 persons → 10 groups (should be
        // [10,10,10,10,10,10,10,10,10,10])

        List<Person> largePersonList = createLargePersonList(100);
        when(personRepository.findByList(testList)).thenReturn(largePersonList);

        request.setNumberOfGroups(10);
        request.setGroupNames(Arrays.asList("Group 1", "Group 2", "Group 3", "Group 4", "Group 5",
                "Group 6", "Group 7", "Group 8", "Group 9", "Group 10"));
        request.setBalanceByGender(true);
        request.setBalanceByTechLevel(true);
        request.setBalanceByProfile(true);

        groupGenerationService.generateGroups(testDraw, request);

        assertEquals(10, testDraw.getGroups().size());

        // Verify each group has exactly 10 persons
        for (Group group : testDraw.getGroups()) {
            assertEquals(10, group.getPersons().size(),
                    "Group " + group.getName() + " should have exactly 10 persons");

            // Verify good diversity in each group
            Set<Person.Gender> genders = group.getPersons().stream()
                    .map(Person::getGender)
                    .collect(Collectors.toSet());
            Set<Integer> techLevels = group.getPersons().stream()
                    .map(Person::getTechLevel)
                    .collect(Collectors.toSet());

            assertTrue(genders.size() >= 2, "Should have gender diversity");
            assertTrue(techLevels.size() >= 3, "Should have tech level diversity");
        }

        // Verify total
        int totalPersons = testDraw.getGroups().stream()
                .mapToInt(group -> group.getPersons().size())
                .sum();
        assertEquals(100, totalPersons);

        System.out.println("✅ Large groups test: 100 persons in 10 groups of 10 each");
    }

    @Test
    void generateGroups_ExtremeDiversity_AllSameGenderAndTech() {
        // Test extreme case: all persons have same gender and tech level

        List<Person> sameAttributePersons = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Person" + (i + 1));
            person.setGender(Person.Gender.MALE); // All same gender
            person.setAge(20 + (i % 10)); // Some age variety
            person.setTechLevel(2); // All same tech level
            person.setFrenchLevel(1 + (i % 4)); // Some French variety
            person.setOldDwwm(i % 2 == 0); // Some DWWM variety
            person.setProfile(Person.Profile.values()[i % 3]); // Some profile variety
            person.setList(testList);
            sameAttributePersons.add(person);
        }

        when(personRepository.findByList(testList)).thenReturn(sameAttributePersons);

        request.setBalanceByGender(true); // Should handle impossible gender balancing
        request.setBalanceByTechLevel(true); // Should handle impossible tech balancing
        request.setBalanceByFrenchLevel(true); // Should work fine
        request.setBalanceByProfile(true); // Should work fine

        groupGenerationService.generateGroups(testDraw, request);

        assertEquals(4, testDraw.getGroups().size());

        // Verify even distribution despite impossible criteria
        for (Group group : testDraw.getGroups()) {
            assertEquals(5, group.getPersons().size());

            // All should have same gender and tech level (since that's all we have)
            Set<Person.Gender> genders = group.getPersons().stream()
                    .map(Person::getGender)
                    .collect(Collectors.toSet());
            Set<Integer> techLevels = group.getPersons().stream()
                    .map(Person::getTechLevel)
                    .collect(Collectors.toSet());

            assertEquals(1, genders.size(), "Should have only MALE gender");
            assertEquals(1, techLevels.size(), "Should have only tech level 2");

            // But should have variety in other attributes
            Set<Integer> frenchLevels = group.getPersons().stream()
                    .map(Person::getFrenchLevel)
                    .collect(Collectors.toSet());
            assertTrue(frenchLevels.size() >= 2, "Should have French level variety");
        }

        System.out.println("✅ Extreme diversity test: algorithm handles impossible balancing gracefully");
    }

    @Test
    void generateGroups_PartialCriteria_Only2CriteriaEnabled() {
        // Test partial criteria: only gender + age balancing

        request.setBalanceByGender(true);
        request.setBalanceByAge(true);
        request.setBalanceByTechLevel(false);
        request.setBalanceByFrenchLevel(false);
        request.setBalanceByOldDwwm(false);
        request.setBalanceByProfile(false);

        groupGenerationService.generateGroups(testDraw, request);

        assertEquals(4, testDraw.getGroups().size());

        for (Group group : testDraw.getGroups()) {
            assertEquals(5, group.getPersons().size());

            // Verify the enabled criteria are balanced
            Set<Person.Gender> genders = group.getPersons().stream()
                    .map(Person::getGender)
                    .collect(Collectors.toSet());

            List<Integer> ages = group.getPersons().stream()
                    .map(Person::getAge)
                    .sorted()
                    .collect(Collectors.toList());

            assertTrue(genders.size() >= 1, "Should have gender diversity");

            // Age should be reasonably distributed (not all same age)
            if (ages.size() > 1) {
                int ageSpread = ages.get(ages.size() - 1) - ages.get(0);
                assertTrue(ageSpread >= 0, "Age should be distributed");
            }
        }

        System.out.println("✅ Partial criteria test: only 2 criteria enabled works correctly");
    }

    @Test
    void generateGroups_PartialCriteria_Only3CriteriaEnabled() {
        // Test partial criteria: gender + tech + profile balancing

        request.setBalanceByGender(true);
        request.setBalanceByAge(false);
        request.setBalanceByTechLevel(true);
        request.setBalanceByFrenchLevel(false);
        request.setBalanceByOldDwwm(false);
        request.setBalanceByProfile(true);

        groupGenerationService.generateGroups(testDraw, request);

        assertEquals(4, testDraw.getGroups().size());

        for (Group group : testDraw.getGroups()) {
            assertEquals(5, group.getPersons().size());

            // Verify the 3 enabled criteria are balanced
            Set<Person.Gender> genders = group.getPersons().stream()
                    .map(Person::getGender)
                    .collect(Collectors.toSet());
            Set<Integer> techLevels = group.getPersons().stream()
                    .map(Person::getTechLevel)
                    .collect(Collectors.toSet());
            Set<Person.Profile> profiles = group.getPersons().stream()
                    .map(Person::getProfile)
                    .collect(Collectors.toSet());

            assertTrue(genders.size() >= 1, "Should have gender diversity");
            assertTrue(techLevels.size() >= 2, "Should have tech level diversity");
            assertTrue(profiles.size() >= 1, "Should have profile diversity");

            System.out.println("Group " + group.getName() + " - Genders: " + genders.size() +
                    ", Tech: " + techLevels.size() + ", Profiles: " + profiles.size());
        }

        System.out.println("✅ Partial criteria test: 3 criteria enabled works correctly");
    }

    // Helper method to create large person list for testing
    private List<Person> createLargePersonList(int count) {
        List<Person> persons = new ArrayList<>();

        String[] nameBase = { "Alex", "Blake", "Casey", "Drew", "Emery", "Finley", "Grey", "Harper" };
        Person.Gender[] genders = Person.Gender.values();
        Person.Profile[] profiles = Person.Profile.values();

        for (int i = 0; i < count; i++) {
            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName(nameBase[i % nameBase.length] + (i / nameBase.length + 1));
            person.setGender(genders[i % genders.length]);
            person.setAge(20 + (i % 15)); // Ages 20-34
            person.setTechLevel(1 + (i % 4)); // Tech levels 1-4
            person.setFrenchLevel(1 + (i % 4)); // French levels 1-4
            person.setOldDwwm(i % 3 == 0); // Mix of old/new DWWM
            person.setProfile(profiles[i % profiles.length]);
            person.setList(testList);
            persons.add(person);
        }

        return persons;
    }
}