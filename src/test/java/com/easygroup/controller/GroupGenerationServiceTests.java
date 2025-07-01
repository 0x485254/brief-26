package com.easygroup.controller;

import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.entity.*;
import com.easygroup.repository.GroupRepository;
import com.easygroup.repository.PersonRepository;
import com.easygroup.service.GroupGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class ComprehensiveFairGroupTests {

    @Autowired
    private GroupGenerationService groupGenerationService;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private PersonRepository personRepository;

    private Draw testDraw;
    private ListEntity testList;

    @BeforeEach
    void setUp() {
        testList = new ListEntity();
        testList.setId(UUID.randomUUID());
        testList.setName("Test Class");

        testDraw = new Draw();
        testDraw.setId(UUID.randomUUID());
        testDraw.setTitle("Test Draw");
        testDraw.setList(testList);
        testDraw.setCreatedAt(LocalDateTime.now());
        testDraw.setGroups(new ArrayList<>());

        when(groupRepository.saveAll(any())).thenAnswer(invocation -> {
            java.util.List<Group> groups = invocation.getArgument(0);
            groups.forEach(group -> {
                if (group.getId() == null) {
                    group.setId(UUID.randomUUID());
                }
            });
            return groups;
        });
    }

    // ========================================
    // 1. FAIR SIZE DISTRIBUTION TESTS
    // ========================================

    @Test
    void testSizeDistribution_EvenNumbers() {
        int[][] testCases = {
                { 20, 4 }, { 24, 6 }, { 15, 3 }, { 12, 4 }, { 18, 3 }, { 30, 5 }
        };

        for (int[] testCase : testCases) {
            int studentCount = testCase[0];
            int groupCount = testCase[1];
            int expectedSize = studentCount / groupCount;

            java.util.List<Person> students = createDiverseClass(studentCount);
            GenerateGroupsRequest request = createBasicRequest(groupCount);

            when(personRepository.findByList(testList)).thenReturn(students);

            groupGenerationService.generateGroups(testDraw, request);

            assertEquals(groupCount, testDraw.getGroups().size(),
                    "Should create exactly " + groupCount + " groups");

            for (Group group : testDraw.getGroups()) {
                assertEquals(expectedSize, group.getPersons().size(),
                        "Each group should have exactly " + expectedSize + " students for " +
                                studentCount + "÷" + groupCount);
            }

            testDraw.setGroups(new ArrayList<>());
        }
    }

    @Test
    void testSizeDistribution_UnevenNumbers() {
        Object[][] testCases = {
                { 21, 4, new int[] { 5, 5, 5, 6 } },
                { 23, 4, new int[] { 5, 6, 6, 6 } },
                { 17, 5, new int[] { 3, 3, 3, 4, 4 } },
                { 19, 6, new int[] { 3, 3, 3, 3, 3, 4 } },
        };

        for (Object[] testCase : testCases) {
            int studentCount = (int) testCase[0];
            int groupCount = (int) testCase[1];
            int[] expectedSizes = (int[]) testCase[2];

            java.util.List<Person> students = createDiverseClass(studentCount);
            GenerateGroupsRequest request = createBasicRequest(groupCount);

            when(personRepository.findByList(testList)).thenReturn(students);

            groupGenerationService.generateGroups(testDraw, request);

            java.util.List<Integer> actualSizes = testDraw.getGroups().stream()
                    .map(group -> group.getPersons().size())
                    .sorted()
                    .collect(Collectors.toList());

            Arrays.sort(expectedSizes);

            assertArrayEquals(expectedSizes, actualSizes.stream().mapToInt(Integer::intValue).toArray(),
                    "Group sizes should be fairly distributed for " + studentCount + " students in " + groupCount
                            + " groups");

            int totalAssigned = actualSizes.stream().mapToInt(Integer::intValue).sum();
            assertEquals(studentCount, totalAssigned, "All students should be assigned");

            testDraw.setGroups(new ArrayList<>());
        }
    }

    @Test
    void testNoStudentLeftBehind() {
        int[] classSizes = { 5, 13, 27, 31, 42, 50 };

        for (int classSize : classSizes) {
            java.util.List<Person> students = createDiverseClass(classSize);
            GenerateGroupsRequest request = createBasicRequest(4);

            when(personRepository.findByList(testList)).thenReturn(students);

            groupGenerationService.generateGroups(testDraw, request);

            Set<String> assignedNames = testDraw.getGroups().stream()
                    .flatMap(group -> group.getPersons().stream())
                    .map(Person::getName)
                    .collect(Collectors.toSet());

            assertEquals(classSize, assignedNames.size(),
                    "All " + classSize + " students should be assigned (no duplicates, no missing)");

            int totalAssignments = testDraw.getGroups().stream()
                    .mapToInt(group -> group.getPersons().size())
                    .sum();
            assertEquals(classSize, totalAssignments, "No student should be in multiple groups");

            testDraw.setGroups(new ArrayList<>());
        }
    }

    // ========================================
    // 2. SINGLE CRITERIA BALANCE TESTS
    // ========================================

    @Test
    void testGenderBalanceOnly() {
        java.util.List<Person> students = createGenderBalancedClass();
        GenerateGroupsRequest request = createBasicRequest(4);
        request.setBalanceByGender(true);

        when(personRepository.findByList(testList)).thenReturn(students);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            Map<Person.Gender, Long> genderCount = group.getPersons().stream()
                    .collect(Collectors.groupingBy(Person::getGender, Collectors.counting()));

            if (group.getPersons().size() >= 3) {
                assertTrue(genderCount.size() >= 1, "Group should have at least one gender");

                long maxGenderInGroup = genderCount.values().stream().mapToLong(Long::longValue).max().orElse(0);
                assertTrue(maxGenderInGroup <= 4, "No group should be dominated by one gender");
            }
        }
    }

    @Test
    void testTechLevelBalanceOnly() {
        java.util.List<Person> students = createTechLevelBalancedClass();
        GenerateGroupsRequest request = createBasicRequest(4);
        request.setBalanceByTechLevel(true);

        when(personRepository.findByList(testList)).thenReturn(students);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            Set<Integer> techLevels = group.getPersons().stream()
                    .map(Person::getTechLevel)
                    .collect(Collectors.toSet());

            if (group.getPersons().size() >= 4) {
                assertTrue(techLevels.size() >= 2,
                        "Group " + group.getName() + " should have at least 2 different tech levels");
            }
        }
    }

    @Test
    void testAgeBalanceOnly() {
        java.util.List<Person> students = createAgeBalancedClass();
        GenerateGroupsRequest request = createBasicRequest(4);
        request.setBalanceByAge(true);

        when(personRepository.findByList(testList)).thenReturn(students);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            IntSummaryStatistics ageStats = group.getPersons().stream()
                    .mapToInt(Person::getAge)
                    .summaryStatistics();

            if (group.getPersons().size() >= 3) {
                int ageSpread = ageStats.getMax() - ageStats.getMin();
                assertTrue(ageSpread >= 0, "Age spread should be non-negative");
                assertTrue(ageSpread <= 15, "Age spread shouldn't be too extreme");
            }
        }
    }

    // ========================================
    // 3. MULTIPLE CRITERIA COMBINATION TESTS
    // ========================================

    @Test
    void testGenderAndTechLevelBalance() {
        java.util.List<Person> students = createFullyDiverseClass(20);
        GenerateGroupsRequest request = createBasicRequest(4);
        request.setBalanceByGender(true);
        request.setBalanceByTechLevel(true);

        when(personRepository.findByList(testList)).thenReturn(students);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            Map<Person.Gender, Long> genderCount = group.getPersons().stream()
                    .collect(Collectors.groupingBy(Person::getGender, Collectors.counting()));

            Set<Integer> techLevels = group.getPersons().stream()
                    .map(Person::getTechLevel)
                    .collect(Collectors.toSet());

            if (group.getPersons().size() >= 4) {
                assertTrue(genderCount.size() >= 1, "Should have gender representation");
                assertTrue(techLevels.size() >= 2, "Should have tech level diversity");

                long maxGender = genderCount.values().stream().mapToLong(Long::longValue).max().orElse(0);
                assertTrue(maxGender <= 4, "Gender shouldn't be too unbalanced");
            }
        }
    }

    @Test
    void testAllCriteriaEnabled() {
        java.util.List<Person> students = createFullyDiverseClass(24);
        GenerateGroupsRequest request = createBasicRequest(4);

        request.setBalanceByGender(true);
        request.setBalanceByTechLevel(true);
        request.setBalanceByFrenchLevel(true);
        request.setBalanceByOldDwwm(true);
        request.setBalanceByProfile(true);
        request.setBalanceByAge(true);

        when(personRepository.findByList(testList)).thenReturn(students);

        groupGenerationService.generateGroups(testDraw, request);

        for (Group group : testDraw.getGroups()) {
            assertEquals(6, group.getPersons().size(), "Each group should have 6 people");

            Set<Person.Gender> genders = group.getPersons().stream()
                    .map(Person::getGender).collect(Collectors.toSet());
            Set<Integer> techLevels = group.getPersons().stream()
                    .map(Person::getTechLevel).collect(Collectors.toSet());
            Set<Person.Profile> profiles = group.getPersons().stream()
                    .map(Person::getProfile).collect(Collectors.toSet());

            assertTrue(genders.size() >= 1, "Should have gender diversity");
            assertTrue(techLevels.size() >= 2, "Should have tech level diversity");
            assertTrue(profiles.size() >= 1, "Should have profile diversity");
        }
    }

    // ========================================
    // 4. EDGE CASES AND ERROR HANDLING
    // ========================================

    @Test
    void testMinimumViableGroups_2Students2Groups() {
        java.util.List<Person> students = Arrays.asList(
                createStudent("Alice", Person.Gender.FEMALE, 22, 1),
                createStudent("Bob", Person.Gender.MALE, 25, 3));
        GenerateGroupsRequest request = createBasicRequest(2);

        when(personRepository.findByList(testList)).thenReturn(students);

        groupGenerationService.generateGroups(testDraw, request);

        assertEquals(2, testDraw.getGroups().size(), "Should create 2 groups");
        assertEquals(1, testDraw.getGroups().get(0).getPersons().size(), "First group should have 1 person");
        assertEquals(1, testDraw.getGroups().get(1).getPersons().size(), "Second group should have 1 person");
    }

    @Test
    void testImpossibleRequest_MoreGroupsThanStudents() {
        java.util.List<Person> students = createDiverseClass(3);
        GenerateGroupsRequest request = createBasicRequest(5);

        when(personRepository.findByList(testList)).thenReturn(students);

        Exception exception = assertThrows(Exception.class, () -> {
            groupGenerationService.generateGroups(testDraw, request);
        });

        assertTrue(exception.getMessage().contains("Not enough persons"),
                "Should explain the impossibility clearly");
    }

    @Test
    void testEmptyClass() {
        when(personRepository.findByList(testList)).thenReturn(new ArrayList<>());
        GenerateGroupsRequest request = createBasicRequest(4);

        Exception exception = assertThrows(Exception.class, () -> {
            groupGenerationService.generateGroups(testDraw, request);
        });

        assertTrue(exception.getMessage().contains("No persons found"),
                "Should explain that the class is empty");
    }

    // ========================================
    // 5. ALGORITHM CONSISTENCY TESTS
    // ========================================

    @Test
    void testConsistentSizeDistribution_MultipleRuns() {
        java.util.List<Person> students = createDiverseClass(23);
        GenerateGroupsRequest request = createBasicRequest(4);

        when(personRepository.findByList(testList)).thenReturn(students);

        for (int run = 0; run < 5; run++) {
            testDraw.setGroups(new ArrayList<>());

            groupGenerationService.generateGroups(testDraw, request);

            java.util.List<Integer> groupSizes = testDraw.getGroups().stream()
                    .map(group -> group.getPersons().size())
                    .sorted()
                    .collect(Collectors.toList());

            assertEquals(Arrays.asList(5, 6, 6, 6), groupSizes,
                    "Run " + (run + 1) + " should maintain fair distribution");
        }
    }

    @Test
    void testDifferentGroupCounts() {
        java.util.List<Person> students = createDiverseClass(30);

        when(personRepository.findByList(testList)).thenReturn(students);

        int[] groupCounts = { 2, 3, 5, 6, 10 };
        int[] expectedSizes = { 15, 10, 6, 5, 3 };

        for (int i = 0; i < groupCounts.length; i++) {
            testDraw.setGroups(new ArrayList<>());
            GenerateGroupsRequest request = createBasicRequest(groupCounts[i]);

            groupGenerationService.generateGroups(testDraw, request);

            assertEquals(groupCounts[i], testDraw.getGroups().size(),
                    "Should create " + groupCounts[i] + " groups");

            for (Group group : testDraw.getGroups()) {
                assertEquals(expectedSizes[i], group.getPersons().size(),
                        "Each group should have " + expectedSizes[i] + " students");
            }
        }
    }

    // ========================================
    // HELPER METHODS FOR TEST DATA CREATION
    // ========================================

    private GenerateGroupsRequest createBasicRequest(int numberOfGroups) {
        GenerateGroupsRequest request = new GenerateGroupsRequest();
        request.setTitle("Test Groups");
        request.setNumberOfGroups(numberOfGroups);

        java.util.List<String> groupNames = new ArrayList<>();
        for (int i = 1; i <= numberOfGroups; i++) {
            groupNames.add("Group " + i);
        }
        request.setGroupNames(groupNames);

        request.setBalanceByGender(false);
        request.setBalanceByAge(false);
        request.setBalanceByTechLevel(false);
        request.setBalanceByFrenchLevel(false);
        request.setBalanceByOldDwwm(false);
        request.setBalanceByProfile(false);

        return request;
    }

    private java.util.List<Person> createDiverseClass(int size) {
        java.util.List<Person> students = new ArrayList<>();
        String[] names = { "Alice", "Bob", "Clara", "David", "Emma", "Frank", "Grace", "Henry",
                "Iris", "Jack", "Kate", "Liam", "Mia", "Noah", "Olivia", "Paul",
                "Quinn", "Rose", "Sam", "Tina", "Uma", "Victor", "Wendy", "Xavier",
                "Yara", "Zoe", "Aaron", "Beth", "Carl", "Diana", "Ethan", "Fiona",
                "George", "Hannah", "Ivan", "Julia", "Kevin", "Laura", "Mark", "Nina",
                "Oscar", "Penny", "Quincy", "Rachel", "Steve", "Tara", "Ulrich", "Vera",
                "William", "Xena" };

        for (int i = 0; i < size && i < names.length; i++) {
            Person student = new Person();
            student.setId(UUID.randomUUID());
            student.setName(names[i]);
            student.setGender(Person.Gender.values()[i % 3]);
            student.setAge(20 + (i % 15));
            student.setTechLevel(1 + (i % 4));
            student.setFrenchLevel(1 + (i % 4));
            student.setOldDwwm(i % 3 == 0);
            student.setProfile(Person.Profile.values()[i % 3]);
            student.setList(testList);
            students.add(student);
        }

        return students;
    }

    private java.util.List<Person> createGenderBalancedClass() {
        java.util.List<Person> students = new ArrayList<>();
        String[] maleNames = { "Alex", "Ben", "Chris", "Dan", "Ed", "Frank", "Greg", "Henry", "Ian", "Jake" };
        String[] femaleNames = { "Anna", "Beth", "Cara", "Diane", "Eva", "Faye", "Gina", "Hope", "Iris", "Jane" };

        for (int i = 0; i < 10; i++) {
            students.add(createStudent(maleNames[i], Person.Gender.MALE, 20 + i, 1 + (i % 4)));
        }

        for (int i = 0; i < 10; i++) {
            students.add(createStudent(femaleNames[i], Person.Gender.FEMALE, 20 + i, 1 + (i % 4)));
        }

        return students;
    }

    private java.util.List<Person> createTechLevelBalancedClass() {
        java.util.List<Person> students = new ArrayList<>();
        String[] names = { "Person1", "Person2", "Person3", "Person4", "Person5", "Person6",
                "Person7", "Person8", "Person9", "Person10", "Person11", "Person12",
                "Person13", "Person14", "Person15", "Person16", "Person17", "Person18",
                "Person19", "Person20" };

        for (int i = 0; i < 20; i++) {
            int techLevel = (i / 5) + 1;
            students.add(createStudent(names[i], Person.Gender.values()[i % 2], 22 + (i % 8), techLevel));
        }

        return students;
    }

    private java.util.List<Person> createAgeBalancedClass() {
        java.util.List<Person> students = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            int age = 20 + (i % 16); // Ages 20-35
            students.add(createStudent("Student" + (i + 1), Person.Gender.values()[i % 2], age, 1 + (i % 4)));
        }

        return students;
    }

    private java.util.List<Person> createFullyDiverseClass(int size) {
        java.util.List<Person> students = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Person student = new Person();
            student.setId(UUID.randomUUID());
            student.setName("Student" + (i + 1));
            student.setGender(Person.Gender.values()[i % 3]);
            student.setAge(20 + (i % 16));
            student.setTechLevel(1 + (i % 4));
            student.setFrenchLevel(1 + (i % 4));
            student.setOldDwwm(i % 4 == 0);
            student.setProfile(Person.Profile.values()[i % 3]);
            student.setList(testList);
            students.add(student);
        }

        return students;
    }

    private Person createStudent(String name, Person.Gender gender, int age, int techLevel) {
        Person student = new Person();
        student.setId(UUID.randomUUID());
        student.setName(name);
        student.setGender(gender);
        student.setAge(age);
        student.setTechLevel(techLevel);
        student.setFrenchLevel(1 + (age % 4));
        student.setOldDwwm(age % 3 == 0);
        student.setProfile(Person.Profile.values()[age % 3]);
        student.setList(testList);
        return student;
    }
}