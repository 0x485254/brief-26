// package com.easygroup.controller;

// import com.easygroup.dto.GenerateGroupsRequest;
// import com.easygroup.entity.*;
// import com.easygroup.repository.GroupRepository;
// import com.easygroup.repository.PersonRepository;
// import com.easygroup.service.GroupGenerationService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.Arguments;
// import org.junit.jupiter.params.provider.MethodSource;
// import org.junit.jupiter.params.provider.ValueSource;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.context.ActiveProfiles;

// import java.time.LocalDateTime;
// import java.util.*;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ActiveProfiles("test")
// @SpringBootTest
// class GroupGenerationServiceTests {

// // ========================================
// // TEST CONSTANTS
// // ========================================
// private static final String DEFAULT_DRAW_TITLE = "Test Draw";
// private static final String DEFAULT_CLASS_NAME = "Test Class";
// private static final String DEFAULT_GROUP_TITLE = "Test Groups";
// private static final int DEFAULT_GROUP_COUNT = 4;
// private static final int STANDARD_CLASS_SIZE = 20;
// private static final int LARGE_CLASS_SIZE = 30;

// @Autowired
// private GroupGenerationService groupGenerationService;

// @MockBean
// private GroupRepository groupRepository;

// @MockBean
// private PersonRepository personRepository;

// private Draw testDraw;
// private ListEntity testList;

// @BeforeEach
// void setUp() {
// testList = new ListEntity();
// testList.setId(UUID.randomUUID());
// testList.setName(DEFAULT_CLASS_NAME);

// testDraw = new Draw();
// testDraw.setId(UUID.randomUUID());
// testDraw.setTitle(DEFAULT_DRAW_TITLE);
// testDraw.setList(testList);
// testDraw.setCreatedAt(LocalDateTime.now());
// testDraw.setGroups(new ArrayList<>());

// when(groupRepository.saveAll(any())).thenAnswer(invocation -> {
// java.util.List<Group> groups = invocation.getArgument(0);
// groups.forEach(group -> {
// if (group.getId() == null) {
// group.setId(UUID.randomUUID());
// }
// });
// return groups;
// });
// }

// // ========================================
// // 1. PARAMETERIZED SIZE DISTRIBUTION TESTS
// // ========================================

// /**
// * Provides test cases for even distribution scenarios
// */
// static Stream<Arguments> evenDistributionTestCases() {
// return Stream.of(
// Arguments.of(20, 4, 5), // 20÷4=5
// Arguments.of(24, 6, 4), // 24÷6=4
// Arguments.of(15, 3, 5), // 15÷3=5
// Arguments.of(12, 4, 3), // 12÷4=3
// Arguments.of(18, 3, 6), // 18÷3=6
// Arguments.of(30, 5, 6) // 30÷5=6
// );
// }

// @ParameterizedTest(name = "Even distribution: {0} students ÷ {1} groups = {2}
// per group")
// @MethodSource("evenDistributionTestCases")
// void testEvenSizeDistribution(int studentCount, int groupCount, int
// expectedSize) {
// // Arrange
// java.util.List<Person> students = createDiverseClass(studentCount);
// GenerateGroupsRequest request = createBasicRequest(groupCount);
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act
// groupGenerationService.generateGroups(testDraw, request);

// // Assert
// assertEquals(groupCount, testDraw.getGroups().size(),
// "Should create exactly " + groupCount + " groups");

// for (Group group : testDraw.getGroups()) {
// assertEquals(expectedSize, group.getPersons().size(),
// "Each group should have exactly " + expectedSize + " students");
// }

// // Verify repository interactions
// verify(personRepository, times(1)).findByList(testList);
// verify(groupRepository, times(1)).saveAll(any());
// verifyNoMoreInteractions(groupRepository, personRepository);
// }

// /**
// * Provides test cases for uneven distribution scenarios
// */
// static Stream<Arguments> unevenDistributionTestCases() {
// return Stream.of(
// Arguments.of(21, 4, new int[] { 5, 5, 5, 6 }), // 21÷4 = 5 remainder 1
// Arguments.of(23, 4, new int[] { 5, 6, 6, 6 }), // 23÷4 = 5 remainder 3
// Arguments.of(17, 5, new int[] { 3, 3, 3, 4, 4 }), // 17÷5 = 3 remainder 2
// Arguments.of(19, 6, new int[] { 3, 3, 3, 3, 3, 4 }), // 19÷6 = 3 remainder 1
// Arguments.of(22, 3, new int[] { 7, 7, 8 }), // 22÷3 = 7 remainder 1
// Arguments.of(25, 4, new int[] { 6, 6, 6, 7 }) // 25÷4 = 6 remainder 1
// );
// }

// @ParameterizedTest(name = "Uneven distribution: {0} students in {1} groups")
// @MethodSource("unevenDistributionTestCases")
// void testUnevenSizeDistribution(int studentCount, int groupCount, int[]
// expectedSizes) {
// // Arrange
// java.util.List<Person> students = createDiverseClass(studentCount);
// GenerateGroupsRequest request = createBasicRequest(groupCount);
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act
// groupGenerationService.generateGroups(testDraw, request);

// // Assert
// java.util.List<Integer> actualSizes = testDraw.getGroups().stream()
// .map(group -> group.getPersons().size())
// .sorted()
// .collect(Collectors.toList());

// Arrays.sort(expectedSizes);
// assertArrayEquals(expectedSizes,
// actualSizes.stream().mapToInt(Integer::intValue).toArray(),
// "Group sizes should be fairly distributed");

// int totalAssigned = actualSizes.stream().mapToInt(Integer::intValue).sum();
// assertEquals(studentCount, totalAssigned, "All students should be assigned");

// // Verify repository interactions
// verify(personRepository, times(1)).findByList(testList);
// verify(groupRepository, times(1)).saveAll(any());
// }

// @ParameterizedTest(name = "No student left behind: {0} students")
// @ValueSource(ints = { 5, 13, 27, 31, 42, 50 })
// void testNoStudentLeftBehind(int classSize) {
// // Arrange
// java.util.List<Person> students = createDiverseClass(classSize);
// GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act
// groupGenerationService.generateGroups(testDraw, request);

// // Assert
// Set<String> assignedNames = testDraw.getGroups().stream()
// .flatMap(group -> group.getPersons().stream())
// .map(Person::getName)
// .collect(Collectors.toSet());

// assertEquals(classSize, assignedNames.size(),
// "All students should be assigned (no duplicates, no missing)");

// int totalAssignments = testDraw.getGroups().stream()
// .mapToInt(group -> group.getPersons().size())
// .sum();
// assertEquals(classSize, totalAssignments, "No student should be in multiple
// groups");

// // Verify repository interactions
// verify(personRepository, times(1)).findByList(testList);
// verify(groupRepository, times(1)).saveAll(any());
// }

// // ========================================
// // 2. BALANCE CRITERIA TESTS
// // ========================================

// @Test
// void testSingleCriteriaBalance_Gender() {
// // Arrange
// java.util.List<Person> students = createGenderBalancedClass();
// GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
// request.setBalanceByGender(true);
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act
// groupGenerationService.generateGroups(testDraw, request);

// // Assert
// for (Group group : testDraw.getGroups()) {
// Map<Person.Gender, Long> genderCount = group.getPersons().stream()
// .collect(Collectors.groupingBy(Person::getGender, Collectors.counting()));

// if (group.getPersons().size() >= 3) {
// assertTrue(genderCount.size() >= 1, "Group should have gender
// representation");
// long maxGenderInGroup =
// genderCount.values().stream().mapToLong(Long::longValue).max().orElse(0);
// assertTrue(maxGenderInGroup <= 4, "No group should be dominated by one
// gender");
// }
// }

// verify(groupRepository, times(1)).saveAll(any());
// }

// @Test
// void testMultipleCriteriaBalance_GenderAndTech() {
// // Arrange
// java.util.List<Person> students =
// createFullyDiverseClass(STANDARD_CLASS_SIZE);
// GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
// request.setBalanceByGender(true);
// request.setBalanceByTechLevel(true);
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act
// groupGenerationService.generateGroups(testDraw, request);

// // Assert
// for (Group group : testDraw.getGroups()) {
// Map<Person.Gender, Long> genderCount = group.getPersons().stream()
// .collect(Collectors.groupingBy(Person::getGender, Collectors.counting()));
// Set<Integer> techLevels = group.getPersons().stream()
// .map(Person::getTechLevel)
// .collect(Collectors.toSet());

// if (group.getPersons().size() >= 4) {
// assertTrue(genderCount.size() >= 1, "Should have gender representation");
// assertTrue(techLevels.size() >= 2, "Should have tech level diversity");
// }
// }

// verify(groupRepository, times(1)).saveAll(argThat(groups -> groups.size() ==
// DEFAULT_GROUP_COUNT &&
// groups.stream().allMatch(group -> !group.getPersons().isEmpty())));
// }

// @Test
// void testAllCriteriaEnabled_MaximumBalance() {
// // Arrange
// java.util.List<Person> students = createFullyDiverseClass(24);
// GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
// enableAllBalanceCriteria(request);
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act
// groupGenerationService.generateGroups(testDraw, request);

// // Assert
// for (Group group : testDraw.getGroups()) {
// assertEquals(6, group.getPersons().size(), "Each group should have 6
// people");

// Set<Person.Gender> genders = group.getPersons().stream()
// .map(Person::getGender).collect(Collectors.toSet());
// Set<Integer> techLevels = group.getPersons().stream()
// .map(Person::getTechLevel).collect(Collectors.toSet());
// Set<Person.Profile> profiles = group.getPersons().stream()
// .map(Person::getProfile).collect(Collectors.toSet());

// assertTrue(genders.size() >= 1, "Should have gender diversity");
// assertTrue(techLevels.size() >= 2, "Should have tech level diversity");
// assertTrue(profiles.size() >= 1, "Should have profile diversity");
// }

// verify(groupRepository, times(1)).saveAll(any());
// }

// // ========================================
// // 3. CONFLICT AND EDGE CASE TESTS
// // ========================================

// @Test
// void testBalancingConflict_InsufficientDiversity() {
// // Arrange: Create a class with very limited diversity
// java.util.List<Person> homogeneousStudents = createHomogeneousClass(12);
// GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
// enableAllBalanceCriteria(request);
// when(personRepository.findByList(testList)).thenReturn(homogeneousStudents);

// // Act & Assert
// assertDoesNotThrow(() -> {
// groupGenerationService.generateGroups(testDraw, request);
// }, "Algorithm should handle limited diversity gracefully");

// // Verify groups are still created despite conflicts
// assertEquals(DEFAULT_GROUP_COUNT, testDraw.getGroups().size());
// int totalAssigned = testDraw.getGroups().stream()
// .mapToInt(group -> group.getPersons().size())
// .sum();
// assertEquals(12, totalAssigned, "All students should still be assigned");
// }

// @Test
// void testMinimumViableScenario_TwoStudentsTwoGroups() {
// // Arrange
// java.util.List<Person> students = Arrays.asList(
// createStudent("Alice", Person.Gender.FEMALE, 22, 1),
// createStudent("Bob", Person.Gender.MALE, 25, 3));
// GenerateGroupsRequest request = createBasicRequest(2);
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act
// groupGenerationService.generateGroups(testDraw, request);

// // Assert
// assertEquals(2, testDraw.getGroups().size(), "Should create 2 groups");
// assertEquals(1, testDraw.getGroups().get(0).getPersons().size());
// assertEquals(1, testDraw.getGroups().get(1).getPersons().size());

// verify(groupRepository, times(1)).saveAll(argThat(groups -> groups.size() ==
// 2));
// }

// // ========================================
// // 4. NEGATIVE TESTING
// // ========================================

// @ParameterizedTest(name = "Invalid group count: {0}")
// @ValueSource(ints = { 0, -1, -5 })
// void testInvalidGroupCount_ThrowsException(int invalidGroupCount) {
// // Arrange
// java.util.List<Person> students = createDiverseClass(10);
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act & Assert
// assertThrows(Exception.class, () -> {
// GenerateGroupsRequest request = createBasicRequest(invalidGroupCount);
// groupGenerationService.generateGroups(testDraw, request);
// }, "Should throw exception for invalid group count");

// verify(personRepository, times(1)).findByList(testList);
// verify(groupRepository, never()).saveAll(any());
// }

// @Test
// void testMismatchedGroupNames_HandlesGracefully() {
// // Arrange
// java.util.List<Person> students = createDiverseClass(12);
// GenerateGroupsRequest request = new GenerateGroupsRequest();
// request.setTitle(DEFAULT_GROUP_TITLE);
// request.setNumberOfGroups(4);
// request.setGroupNames(Arrays.asList("Group 1", "Group 2")); // Only 2 names
// for 4 groups
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act & Assert
// assertThrows(Exception.class, () -> {
// groupGenerationService.generateGroups(testDraw, request);
// }, "Should handle mismatched group names appropriately");
// }

// @Test
// void testTooManyGroups_ThrowsException() {
// // Arrange
// java.util.List<Person> students = createDiverseClass(3);
// GenerateGroupsRequest request = createBasicRequest(5); // 5 groups for 3
// students
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act & Assert
// Exception exception = assertThrows(Exception.class, () -> {
// groupGenerationService.generateGroups(testDraw, request);
// });

// assertTrue(exception.getMessage().contains("Not enough persons"),
// "Should explain the impossibility clearly");
// verify(personRepository, times(1)).findByList(testList);
// verify(groupRepository, never()).saveAll(any());
// }

// @Test
// void testEmptyClass_ThrowsException() {
// // Arrange
// when(personRepository.findByList(testList)).thenReturn(new ArrayList<>());
// GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);

// // Act & Assert
// Exception exception = assertThrows(Exception.class, () -> {
// groupGenerationService.generateGroups(testDraw, request);
// });

// assertTrue(exception.getMessage().contains("No persons found"),
// "Should explain that the class is empty");
// verify(personRepository, times(1)).findByList(testList);
// verify(groupRepository, never()).saveAll(any());
// }

// // ========================================
// // 5. ALGORITHM CONSISTENCY TESTS
// // ========================================

// @Test
// void testConsistentSizeDistribution_MultipleRuns() {
// // Arrange
// java.util.List<Person> students = createDiverseClass(23);
// GenerateGroupsRequest request = createBasicRequest(DEFAULT_GROUP_COUNT);
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act & Assert
// for (int run = 0; run < 5; run++) {
// testDraw.setGroups(new ArrayList<>());

// groupGenerationService.generateGroups(testDraw, request);

// java.util.List<Integer> groupSizes = testDraw.getGroups().stream()
// .map(group -> group.getPersons().size())
// .sorted()
// .collect(Collectors.toList());

// assertEquals(Arrays.asList(5, 6, 6, 6), groupSizes,
// "Run " + (run + 1) + " should maintain fair distribution");
// }

// verify(personRepository, times(5)).findByList(testList);
// verify(groupRepository, times(5)).saveAll(any());
// }

// @ParameterizedTest(name = "Different group counts: {0} groups for {1}
// students")
// @MethodSource("differentGroupCountTestCases")
// void testDifferentGroupCounts(int groupCount, int studentCount, int
// expectedSize) {
// // Arrange
// java.util.List<Person> students = createDiverseClass(studentCount);
// GenerateGroupsRequest request = createBasicRequest(groupCount);
// when(personRepository.findByList(testList)).thenReturn(students);

// // Act
// groupGenerationService.generateGroups(testDraw, request);

// // Assert
// assertEquals(groupCount, testDraw.getGroups().size(),
// "Should create " + groupCount + " groups");

// for (Group group : testDraw.getGroups()) {
// assertEquals(expectedSize, group.getPersons().size(),
// "Each group should have " + expectedSize + " students");
// }

// verify(groupRepository, times(1)).saveAll(any());
// }

// static Stream<Arguments> differentGroupCountTestCases() {
// return Stream.of(
// Arguments.of(2, 30, 15), // 2 groups of 15
// Arguments.of(3, 30, 10), // 3 groups of 10
// Arguments.of(5, 30, 6), // 5 groups of 6
// Arguments.of(6, 30, 5), // 6 groups of 5
// Arguments.of(10, 30, 3) // 10 groups of 3
// );
// }

// // ========================================
// // HELPER METHODS
// // ========================================

// private GenerateGroupsRequest createBasicRequest(int numberOfGroups) {
// GenerateGroupsRequest request = new GenerateGroupsRequest();
// request.setTitle(DEFAULT_GROUP_TITLE);
// request.setNumberOfGroups(numberOfGroups);

// java.util.List<String> groupNames = new ArrayList<>();
// for (int i = 1; i <= numberOfGroups; i++) {
// groupNames.add("Group " + i);
// }
// request.setGroupNames(groupNames);

// // All criteria disabled by default
// request.setBalanceByGender(false);
// request.setBalanceByAge(false);
// request.setBalanceByTechLevel(false);
// request.setBalanceByFrenchLevel(false);
// request.setBalanceByOldDwwm(false);
// request.setBalanceByProfile(false);

// return request;
// }

// private void enableAllBalanceCriteria(GenerateGroupsRequest request) {
// request.setBalanceByGender(true);
// request.setBalanceByAge(true);
// request.setBalanceByTechLevel(true);
// request.setBalanceByFrenchLevel(true);
// request.setBalanceByOldDwwm(true);
// request.setBalanceByProfile(true);
// }

// private java.util.List<Person> createDiverseClass(int size) {
// java.util.List<Person> students = new ArrayList<>();
// String[] names = { "Alice", "Bob", "Clara", "David", "Emma", "Frank",
// "Grace", "Henry",
// "Iris", "Jack", "Kate", "Liam", "Mia", "Noah", "Olivia", "Paul",
// "Quinn", "Rose", "Sam", "Tina", "Uma", "Victor", "Wendy", "Xavier",
// "Yara", "Zoe", "Aaron", "Beth", "Carl", "Diana", "Ethan", "Fiona",
// "George", "Hannah", "Ivan", "Julia", "Kevin", "Laura", "Mark", "Nina",
// "Oscar", "Penny", "Quincy", "Rachel", "Steve", "Tara", "Ulrich", "Vera",
// "William", "Xena" };

// for (int i = 0; i < size && i < names.length; i++) {
// Person student = new Person();
// student.setId(UUID.randomUUID());
// student.setName(names[i]);
// student.setGender(Person.Gender.values()[i % 3]);
// student.setAge(20 + (i % 15));
// student.setTechLevel(1 + (i % 4));
// student.setFrenchLevel(1 + (i % 4));
// student.setOldDwwm(i % 3 == 0);
// student.setProfile(Person.Profile.values()[i % 3]);
// student.setList(testList);
// students.add(student);
// }

// return students;
// }

// private java.util.List<Person> createGenderBalancedClass() {
// java.util.List<Person> students = new ArrayList<>();
// String[] maleNames = { "Alex", "Ben", "Chris", "Dan", "Ed", "Frank", "Greg",
// "Henry", "Ian", "Jake" };
// String[] femaleNames = { "Anna", "Beth", "Cara", "Diane", "Eva", "Faye",
// "Gina", "Hope", "Iris", "Jane" };

// for (int i = 0; i < 10; i++) {
// students.add(createStudent(maleNames[i], Person.Gender.MALE, 20 + i, 1 + (i %
// 4)));
// }

// for (int i = 0; i < 10; i++) {
// students.add(createStudent(femaleNames[i], Person.Gender.FEMALE, 20 + i, 1 +
// (i % 4)));
// }

// return students;
// }

// private java.util.List<Person> createFullyDiverseClass(int size) {
// java.util.List<Person> students = new ArrayList<>();

// for (int i = 0; i < size; i++) {
// Person student = new Person();
// student.setId(UUID.randomUUID());
// student.setName("Student" + (i + 1));
// student.setGender(Person.Gender.values()[i % 3]);
// student.setAge(20 + (i % 16));
// student.setTechLevel(1 + (i % 4));
// student.setFrenchLevel(1 + (i % 4));
// student.setOldDwwm(i % 4 == 0);
// student.setProfile(Person.Profile.values()[i % 3]);
// student.setList(testList);
// students.add(student);
// }

// return students;
// }

// private java.util.List<Person> createHomogeneousClass(int size) {
// java.util.List<Person> students = new ArrayList<>();

// for (int i = 0; i < size; i++) {
// Person student = new Person();
// student.setId(UUID.randomUUID());
// student.setName("Student" + (i + 1));
// student.setGender(Person.Gender.MALE); // All same gender
// student.setAge(25); // All same age
// student.setTechLevel(2); // All same tech level
// student.setFrenchLevel(3); // All same French level
// student.setOldDwwm(false); // All same DWWM status
// student.setProfile(Person.Profile.A_LAISE); // All same profile
// student.setList(testList);
// students.add(student);
// }

// return students;
// }

// private Person createStudent(String name, Person.Gender gender, int age, int
// techLevel) {
// Person student = new Person();
// student.setId(UUID.randomUUID());
// student.setName(name);
// student.setGender(gender);
// student.setAge(age);
// student.setTechLevel(techLevel);
// student.setFrenchLevel(1 + (age % 4));
// student.setOldDwwm(age % 3 == 0);
// student.setProfile(Person.Profile.values()[age % 3]);
// student.setList(testList);
// return student;
// }
// }