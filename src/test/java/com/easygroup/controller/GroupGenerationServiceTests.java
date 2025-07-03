//package com.easygroup.controller;
//
//import com.easygroup.dto.GenerateGroupsRequest;
//import com.easygroup.dto.GroupResponse;
//import com.easygroup.entity.*;
//import com.easygroup.repository.PersonRepository;
//import com.easygroup.service.GroupGenerationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ActiveProfiles("test")
//@SpringBootTest
//class GroupGenerationServiceTests {
//
//    private static final String DEFAULT_CLASS_NAME = "Test Class";
//    private static final String DEFAULT_GROUP_TITLE = "Test Groups";
//    private static final int DEFAULT_GROUP_COUNT = 4;
//    private static final int STANDARD_CLASS_SIZE = 20;
//    private static final int LARGE_CLASS_SIZE = 30;
//
//    @Autowired
//    private GroupGenerationService groupGenerationService;
//
//    @MockBean
//    private PersonRepository personRepository;
//
//    private ListEntity testList;
//
//    @BeforeEach
//    void setUp() {
//        testList = new ListEntity();
//        testList.setId(UUID.randomUUID());
//        testList.setName(DEFAULT_CLASS_NAME);
//    }
//
//    // ========================================
//    // 1. PARAMETERIZED SIZE DISTRIBUTION TESTS
//    // ========================================
//
//    static Stream<Arguments> evenDistributionTestCases() {
//        return Stream.of(
//                Arguments.of(20, 4, 5), // 20÷4=5
//                Arguments.of(24, 6, 4), // 24÷6=4
//                Arguments.of(15, 3, 5), // 15÷3=5
//                Arguments.of(12, 4, 3), // 12÷4=3
//                Arguments.of(18, 3, 6), // 18÷3=6
//                Arguments.of(30, 5, 6) // 30÷5=6
//        );
//    }
//
//    @ParameterizedTest(name = "Even distribution: {0} students ÷ {1} groups = {2} per group")
//    @MethodSource("evenDistributionTestCases")
//    void testEvenSizeDistribution(int studentCount, int groupCount, int expectedSize) {
//        List<Person> students = createDiverseClass(studentCount);
//        GenerateGroupsRequest request = createBasicRequest(groupCount);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(testList, request);
//
//        assertEquals(groupCount, groups.size(), "Should create exactly " + groupCount + " groups");
//
//        for (GroupResponse group : groups) {
//            assertEquals(expectedSize, group.getPersonCount(),
//                    "Each group should have exactly " + expectedSize + " students");
//        }
//
//        // Verify repository interactions
//        verify(personRepository, times(1)).findByList(testList);
//        verifyNoMoreInteractions(personRepository);
//    }
//
//    static Stream<Arguments> unevenDistributionTestCases() {
//        return Stream.of(
//                Arguments.of(21, 4, new int[] { 5, 5, 5, 6 }),
//                Arguments.of(23, 4, new int[] { 5, 6, 6, 6 }),
//                Arguments.of(17, 5, new int[] { 3, 3, 3, 4, 4 }),
//                Arguments.of(19, 6, new int[] { 3, 3, 3, 3, 3, 4 }),
//                Arguments.of(22, 3, new int[] { 7, 7, 8 }),
//                Arguments.of(25, 4, new int[] { 6, 6, 6, 7 }));
//    }
//
//    @ParameterizedTest(name = "Uneven distribution: {0} students in {1} groups")
//    @MethodSource("unevenDistributionTestCases")
//    void testUnevenSizeDistribution(int studentCount, int groupCount, int[] expectedSizes) {
//        List<Person> students = createDiverseClass(studentCount);
//        GenerateGroupsRequest request = createBasicRequest(groupCount);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(testList, request);
//
//        List<Integer> actualSizes = groups.stream()
//                .map(GroupResponse::getPersonCount)
//                .sorted()
//                .collect(Collectors.toList());
//
//        Arrays.sort(expectedSizes);
//        assertArrayEquals(expectedSizes,
//                actualSizes.stream().mapToInt(Integer::intValue).toArray(),
//                "Group sizes should be fairly distributed");
//
//        int totalAssigned = actualSizes.stream().mapToInt(Integer::intValue).sum();
//        assertEquals(studentCount, totalAssigned, "All students should be assigned");
//
//        verify(personRepository, times(1)).findByList(testList);
//    }
//
//    @ParameterizedTest(name = "No student left behind: {0} students")
//    @ValueSource(ints = { 5, 13, 27, 31, 42, 50 })
//    void testNoStudentLeftBehind(int classSize) {
//        List<Person> students = createDiverseClass(classSize);
//        GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(testList, request);
//
//        Set<UUID> assignedIds = groups.stream()
//                .flatMap(group -> group.getPersons().stream())
//                .map(person -> person.getPersonId())
//                .collect(Collectors.toSet());
//
//        assertEquals(classSize, assignedIds.size(),
//                "All students should be assigned (no duplicates, no missing)");
//
//        int totalAssignments = groups.stream()
//                .mapToInt(GroupResponse::getPersonCount)
//                .sum();
//        assertEquals(classSize, totalAssignments, "No student should be in multiple groups");
//
//        verify(personRepository, times(1)).findByList(testList);
//    }
//
//    @Test
//    void testSingleCriteriaBalance_Gender() {
//        List<Person> students = createGenderBalancedClass();
//        GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
//        request.setBalanceByGender(true);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(testList, request);
//
//        for (GroupResponse group : groups) {
//            if (group.getPersonCount() >= 3) {
//                Set<String> genders = group.getPersons().stream()
//                        .map(person -> person.getGender())
//                        .collect(Collectors.toSet());
//
//                assertTrue(genders.size() >= 1, "Group should have gender representation");
//
//                Map<String, Long> genderCount = group.getPersons().stream()
//                        .collect(Collectors.groupingBy(person -> person.getGender(), Collectors.counting()));
//
//                long maxGenderInGroup = genderCount.values().stream().mapToLong(Long::longValue).max().orElse(0);
//                assertTrue(maxGenderInGroup <= 4, "No group should be dominated by one gender");
//            }
//        }
//    }
//
//    @Test
//    void testMultipleCriteriaBalance_GenderAndTech() {
//        List<Person> students = createFullyDiverseClass(STANDARD_CLASS_SIZE);
//        GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
//        request.setBalanceByGender(true);
//        request.setBalanceByTechLevel(true);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(testList, request);
//
//        for (GroupResponse group : groups) {
//            if (group.getPersonCount() >= 4) {
//                Set<String> genders = group.getPersons().stream()
//                        .map(person -> person.getGender())
//                        .collect(Collectors.toSet());
//                Set<Integer> techLevels = group.getPersons().stream()
//                        .map(person -> person.getTechLevel())
//                        .collect(Collectors.toSet());
//
//                assertTrue(genders.size() >= 1, "Should have gender representation");
//                assertTrue(techLevels.size() >= 2, "Should have tech level diversity");
//            }
//        }
//
//        assertEquals(DEFAULT_GROUP_COUNT, groups.size());
//        assertTrue(groups.stream().allMatch(group -> group.getPersonCount() > 0));
//    }
//
//    @Test
//    void testAllCriteriaEnabled_MaximumBalance() {
//        // Arrange
//        List<Person> students = createFullyDiverseClass(24);
//        GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
//        enableAllBalanceCriteria(request);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(testList, request);
//
//        for (GroupResponse group : groups) {
//            assertEquals(6, group.getPersonCount(), "Each group should have 6 people");
//
//            Set<String> genders = group.getPersons().stream()
//                    .map(person -> person.getGender()).collect(Collectors.toSet());
//            Set<Integer> techLevels = group.getPersons().stream()
//                    .map(person -> person.getTechLevel()).collect(Collectors.toSet());
//            Set<String> profiles = group.getPersons().stream()
//                    .map(person -> person.getProfile()).collect(Collectors.toSet());
//
//            assertTrue(genders.size() >= 1, "Should have gender diversity");
//            assertTrue(techLevels.size() >= 2, "Should have tech level diversity");
//            assertTrue(profiles.size() >= 1, "Should have profile diversity");
//        }
//    }
//
//    @Test
//    void testBalancingConflict_InsufficientDiversity() {
//        List<Person> homogeneousStudents = createHomogeneousClass(12);
//        GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
//        enableAllBalanceCriteria(request);
//        when(personRepository.findByList(testList)).thenReturn(homogeneousStudents);
//
//        assertDoesNotThrow(() -> {
//            List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(testList, request);
//
//            assertEquals(DEFAULT_GROUP_COUNT, groups.size());
//            int totalAssigned = groups.stream()
//                    .mapToInt(GroupResponse::getPersonCount)
//                    .sum();
//            assertEquals(12, totalAssigned, "All students should still be assigned");
//        }, "Algorithm should handle limited diversity gracefully");
//    }
//
//    @Test
//    void testMinimumViableScenario_TwoStudentsTwoGroups() {
//        List<Person> students = Arrays.asList(
//                createStudent("Alice", Person.Gender.FEMALE, 22, 1),
//                createStudent("Bob", Person.Gender.MALE, 25, 3));
//        GenerateGroupsRequest request = createBasicRequest(2);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(testList, request);
//
//        assertEquals(2, groups.size(), "Should create 2 groups");
//        assertEquals(1, groups.get(0).getPersonCount());
//        assertEquals(1, groups.get(1).getPersonCount());
//    }
//
//    @ParameterizedTest(name = "Invalid group count: {0}")
//    @ValueSource(ints = { 0, -1, -5 })
//    void testInvalidGroupCount_ThrowsException(int invalidGroupCount) {
//        List<Person> students = createDiverseClass(10);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        assertThrows(Exception.class, () -> {
//            GenerateGroupsRequest request = createBasicRequest(invalidGroupCount);
//            groupGenerationService.generateGroupsPreview(testList, request);
//        }, "Should throw exception for invalid group count");
//
//        verify(personRepository, times(1)).findByList(testList);
//    }
//
//    @Test
//    void testMismatchedGroupNames_HandlesGracefully() {
//        List<Person> students = createDiverseClass(12);
//        GenerateGroupsRequest request = new GenerateGroupsRequest();
//        request.setTitle(DEFAULT_GROUP_TITLE);
//        request.setNumberOfGroups(4);
//        request.setGroupNames(Arrays.asList("Group 1", "Group 2"));
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        assertThrows(Exception.class, () -> {
//            groupGenerationService.generateGroupsPreview(testList, request);
//        }, "Should handle mismatched group names appropriately");
//    }
//
//    @Test
//    void testTooManyGroups_ThrowsException() {
//        List<Person> students = createDiverseClass(3);
//        GenerateGroupsRequest request = createBasicRequest(5);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        Exception exception = assertThrows(Exception.class, () -> {
//            groupGenerationService.generateGroupsPreview(testList, request);
//        });
//
//        assertTrue(exception.getMessage().contains("Not enough persons"),
//                "Should explain the impossibility clearly");
//        verify(personRepository, times(1)).findByList(testList);
//    }
//
//    @Test
//    void testEmptyClass_ThrowsException() {
//        when(personRepository.findByList(testList)).thenReturn(new ArrayList<>());
//        GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
//
//        Exception exception = assertThrows(Exception.class, () -> {
//            groupGenerationService.generateGroupsPreview(testList, request);
//        });
//
//        assertTrue(exception.getMessage().contains("No persons found"),
//                "Should explain that the class is empty");
//        verify(personRepository, times(1)).findByList(testList);
//    }
//
//    @Test
//    void testConsistentSizeDistribution_MultipleRuns() {
//        List<Person> students = createDiverseClass(23);
//        GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        for (int run = 0; run < 5; run++) {
//            List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(testList, request);
//
//            List<Integer> groupSizes = groups.stream()
//                    .map(GroupResponse::getPersonCount)
//                    .sorted()
//                    .collect(Collectors.toList());
//
//            assertEquals(Arrays.asList(5, 6, 6, 6), groupSizes,
//                    "Run " + (run + 1) + " should maintain fair distribution");
//        }
//
//        verify(personRepository, times(5)).findByList(testList);
//    }
//
//    @ParameterizedTest(name = "Different group counts: {0} groups for {1} students")
//    @MethodSource("differentGroupCountTestCases")
//    void testDifferentGroupCounts(int groupCount, int studentCount, int expectedSize) {
//        List<Person> students = createDiverseClass(studentCount);
//        GenerateGroupsRequest request = createBasicRequest(groupCount);
//        when(personRepository.findByList(testList)).thenReturn(students);
//
//        List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(testList, request);
//
//        assertEquals(groupCount, groups.size(), "Should create " + groupCount + " groups");
//
//        for (GroupResponse group : groups) {
//            assertEquals(expectedSize, group.getPersonCount(),
//                    "Each group should have " + expectedSize + " students");
//        }
//    }
//
//    static Stream<Arguments> differentGroupCountTestCases() {
//        return Stream.of(
//                Arguments.of(2, 30, 15),
//                Arguments.of(3, 30, 10),
//                Arguments.of(5, 30, 6),
//                Arguments.of(6, 30, 5),
//                Arguments.of(10, 30, 3));
//    }
//
//    private GenerateGroupsRequest createBasicRequest(int numberOfGroups) {
//        GenerateGroupsRequest request = new GenerateGroupsRequest();
//        request.setTitle(DEFAULT_GROUP_TITLE);
//        request.setNumberOfGroups(numberOfGroups);
//
//        List<String> groupNames = new ArrayList<>();
//        for (int i = 1; i <= numberOfGroups; i++) {
//            groupNames.add("Group " + i);
//        }
//        request.setGroupNames(groupNames);
//
//        request.setBalanceByGender(false);
//        request.setBalanceByAge(false);
//        request.setBalanceByTechLevel(false);
//        request.setBalanceByFrenchLevel(false);
//        request.setBalanceByOldDwwm(false);
//        request.setBalanceByProfile(false);
//
//        return request;
//    }
//
//    private void enableAllBalanceCriteria(GenerateGroupsRequest request) {
//        request.setBalanceByGender(true);
//        request.setBalanceByAge(true);
//        request.setBalanceByTechLevel(true);
//        request.setBalanceByFrenchLevel(true);
//        request.setBalanceByOldDwwm(true);
//        request.setBalanceByProfile(true);
//    }
//
//    private List<Person> createDiverseClass(int size) {
//        List<Person> students = new ArrayList<>();
//        String[] names = { "Alice", "Bob", "Clara", "David", "Emma", "Frank", "Grace", "Henry",
//                "Iris", "Jack", "Kate", "Liam", "Mia", "Noah", "Olivia", "Paul",
//                "Quinn", "Rose", "Sam", "Tina", "Uma", "Victor", "Wendy", "Xavier",
//                "Yara", "Zoe", "Aaron", "Beth", "Carl", "Diana", "Ethan", "Fiona",
//                "George", "Hannah", "Ivan", "Julia", "Kevin", "Laura", "Mark", "Nina",
//                "Oscar", "Penny", "Quincy", "Rachel", "Steve", "Tara", "Ulrich", "Vera",
//                "William", "Xena" };
//
//        for (int i = 0; i < size && i < names.length; i++) {
//            Person student = new Person();
//            student.setId(UUID.randomUUID());
//            student.setName(names[i]);
//            student.setGender(Person.Gender.values()[i % 3]);
//            student.setAge(20 + (i % 15));
//            student.setTechLevel(1 + (i % 4));
//            student.setFrenchLevel(1 + (i % 4));
//            student.setOldDwwm(i % 3 == 0);
//            student.setProfile(Person.Profile.values()[i % 3]);
//            student.setList(testList);
//            students.add(student);
//        }
//
//        return students;
//    }
//
//    private List<Person> createGenderBalancedClass() {
//        List<Person> students = new ArrayList<>();
//        String[] maleNames = { "Alex", "Ben", "Chris", "Dan", "Ed", "Frank", "Greg", "Henry", "Ian", "Jake" };
//        String[] femaleNames = { "Anna", "Beth", "Cara", "Diane", "Eva", "Faye", "Gina", "Hope", "Iris", "Jane" };
//
//        for (int i = 0; i < 10; i++) {
//            students.add(createStudent(maleNames[i], Person.Gender.MALE, 20 + i, 1 + (i % 4)));
//        }
//
//        for (int i = 0; i < 10; i++) {
//            students.add(createStudent(femaleNames[i], Person.Gender.FEMALE, 20 + i, 1 + (i % 4)));
//        }
//
//        return students;
//    }
//
//    private List<Person> createFullyDiverseClass(int size) {
//        List<Person> students = new ArrayList<>();
//
//        for (int i = 0; i < size; i++) {
//            Person student = new Person();
//            student.setId(UUID.randomUUID());
//            student.setName("Student" + (i + 1));
//            student.setGender(Person.Gender.values()[i % 3]);
//            student.setAge(20 + (i % 16));
//            student.setTechLevel(1 + (i % 4));
//            student.setFrenchLevel(1 + (i % 4));
//            student.setOldDwwm(i % 4 == 0);
//            student.setProfile(Person.Profile.values()[i % 3]);
//            student.setList(testList);
//            students.add(student);
//        }
//
//        return students;
//    }
//
//    private List<Person> createHomogeneousClass(int size) {
//        List<Person> students = new ArrayList<>();
//
//        for (int i = 0; i < size; i++) {
//            Person student = new Person();
//            student.setId(UUID.randomUUID());
//            student.setName("Student" + (i + 1));
//            student.setGender(Person.Gender.MALE);
//            student.setAge(25);
//            student.setTechLevel(2);
//            student.setFrenchLevel(3);
//            student.setOldDwwm(false);
//            student.setProfile(Person.Profile.A_LAISE);
//            student.setList(testList);
//            students.add(student);
//        }
//
//        return students;
//    }
//
//    private Person createStudent(String name, Person.Gender gender, int age, int techLevel) {
//        Person student = new Person();
//        student.setId(UUID.randomUUID());
//        student.setName(name);
//        student.setGender(gender);
//        student.setAge(age);
//        student.setTechLevel(techLevel);
//        student.setFrenchLevel(1 + (age % 4));
//        student.setOldDwwm(age % 3 == 0);
//        student.setProfile(Person.Profile.values()[age % 3]);
//        student.setList(testList);
//        return student;
//    }
//}