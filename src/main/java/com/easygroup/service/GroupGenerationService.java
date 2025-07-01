package com.easygroup.service;

import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.dto.GroupResponse;
import com.easygroup.entity.Draw;
import com.easygroup.entity.Group;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.Person;
import com.easygroup.mapper.GroupMapper;
import com.easygroup.repository.GroupRepository;
import com.easygroup.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupGenerationService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PersonRepository personRepository;

    public void generateGroups(Draw draw, GenerateGroupsRequest request) {
        System.out.println("Starting group generation for draw: " + draw.getTitle());
        System.out.println("Request: " + request);

        List<Person> persons = personRepository.findByList(draw.getList());
        System.out.println("Found " + persons.size() + " persons in list: " + draw.getList().getName());

        if (persons.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot create groups: No persons found in the list");
        }

        if (persons.size() < request.getNumberOfGroups()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not enough persons to create " + request.getNumberOfGroups() + " groups. Found: "
                            + persons.size());
        }

        List<Group> groups = createEmptyGroups(request, draw);

        distributePersons(persons, groups, request);

        draw.setGroups(groups);
        groupRepository.saveAll(groups);

        System.out.println("Group generation completed ");
    }

    private List<Group> createEmptyGroups(GenerateGroupsRequest request, Draw draw) {
        System.out.println("Creating " + request.getNumberOfGroups() + " empty groups");

        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < request.getNumberOfGroups(); i++) {
            Group group = new Group();
            group.setName(request.getGroupNames().get(i));
            group.setDraw(draw);
            group.setPersons(new ArrayList<>());
            groups.add(group);
        }

        return groups;
    }

    private void distributePersons(List<Person> persons, List<Group> groups,
            GenerateGroupsRequest request) {
        System.out.println("Starting distribution");

        int totalPersons = persons.size();
        int numberOfGroups = groups.size();
        int baseSize = totalPersons / numberOfGroups;
        int remainder = totalPersons % numberOfGroups;

        int[] targetSizes = new int[numberOfGroups];
        for (int i = 0; i < numberOfGroups; i++) {
            targetSizes[i] = baseSize + (i < remainder ? 1 : 0);
        }

        System.out.println("Target group sizes: " + Arrays.toString(targetSizes));

        List<Person> shuffledPersons = new ArrayList<>(persons);
        Collections.shuffle(shuffledPersons);

        for (Person person : shuffledPersons) {
            Group bestGroup = findBestGroup(groups, targetSizes, person, request);
            if (bestGroup != null) {
                bestGroup.getPersons().add(person);
                System.out.println("Assigned " + person.getName() + " to " + bestGroup.getName());
            }
        }

        verifyDistribution(groups, targetSizes);
    }

    private Group findBestGroup(List<Group> groups, int[] targetSizes,
            Person person, GenerateGroupsRequest request) {

        List<Group> availableGroups = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getPersons().size() < targetSizes[i]) {
                availableGroups.add(groups.get(i));
            }
        }

        if (availableGroups.isEmpty()) {
            return null;
        }

        Group bestGroup = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Group group : availableGroups) {
            double balanceScore = calculateBalanceScore(group, person, request);

            int groupIndex = groups.indexOf(group);
            double sizeScore = (targetSizes[groupIndex] - group.getPersons().size()) * 10.0;

            double totalScore = balanceScore + sizeScore;

            System.out.println("Group " + group.getName() + " score for " + person.getName() +
                    ": balance=" + balanceScore + ", size=" + sizeScore + ", total=" + totalScore);

            if (totalScore > bestScore) {
                bestScore = totalScore;
                bestGroup = group;
            }
        }

        return bestGroup;
    }

    private double calculateBalanceScore(Group group, Person person, GenerateGroupsRequest request) {
        double score = 0.0;

        if (request.getBalanceByGender()) {
            score += calculateAttributeDiversityScore(group, person, Person::getGender, "Gender");
        }

        if (request.getBalanceByTechLevel()) {
            score += calculateAttributeDiversityScore(group, person, Person::getTechLevel, "TechLevel");
        }

        if (request.getBalanceByFrenchLevel()) {
            score += calculateAttributeDiversityScore(group, person, Person::getFrenchLevel, "FrenchLevel");
        }

        if (request.getBalanceByOldDwwm()) {
            score += calculateAttributeDiversityScore(group, person, Person::getOldDwwm, "OldDwwm");
        }

        if (request.getBalanceByProfile()) {
            score += calculateAttributeDiversityScore(group, person, Person::getProfile, "Profile");
        }

        if (request.getBalanceByAge()) {
            score += calculateAgeDiversityScore(group, person);
        }

        return score;
    }

    private <T> double calculateAttributeDiversityScore(Group group, Person person,
            java.util.function.Function<Person, T> attributeGetter, String attributeName) {

        T personAttribute = attributeGetter.apply(person);

        long countWithSameAttribute = group.getPersons().stream()
                .filter(p -> attributeGetter.apply(p).equals(personAttribute))
                .count();

        double diversityScore = 10.0 - countWithSameAttribute;

        System.out.println("  " + attributeName + " diversity for " + person.getName() +
                " (" + personAttribute + "): existing=" + countWithSameAttribute +
                ", score=" + diversityScore);

        return diversityScore;
    }

    private double calculateAgeDiversityScore(Group group, Person person) {
        if (group.getPersons().isEmpty()) {
            return 5.0;
        }

        List<Integer> groupAges = group.getPersons().stream()
                .map(Person::getAge)
                .collect(Collectors.toList());

        double avgAge = groupAges.stream().mapToInt(Integer::intValue).average().orElse(0);
        double ageDifference = Math.abs(person.getAge() - avgAge);

        double ageScore;
        if (ageDifference >= 2 && ageDifference <= 5) {
            ageScore = 5.0;
        } else if (ageDifference < 2) {
            ageScore = 1.0;
        } else {
            ageScore = 3.0;
        }

        System.out.println("  Age diversity for " + person.getName() +
                " (age " + person.getAge() + "): avgGroupAge=" + avgAge +
                ", difference=" + ageDifference + ", score=" + ageScore);

        return ageScore;
    }

    private void verifyDistribution(List<Group> groups, int[] targetSizes) {
        System.out.println("Final distribution verification:");
        for (int i = 0; i < groups.size(); i++) {
            int actualSize = groups.get(i).getPersons().size();
            int targetSize = targetSizes[i];
            System.out.println(groups.get(i).getName() + ": " + actualSize + "/" + targetSize + " persons");

            if (actualSize != targetSize) {
                System.out.println("WARNING: Group " + groups.get(i).getName() +
                        " has " + actualSize + " persons but target was " + targetSize);
            }
        }

    }

    public List<GroupResponse> generateGroupsPreview(ListEntity list, GenerateGroupsRequest request) {
        System.out.println("Starting PREVIEW group generation for list: " + list.getName());

        List<Person> persons = personRepository.findByList(list);

        if (persons.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot create groups: No persons found in the list");
        }

        if (persons.size() < request.getNumberOfGroups()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not enough persons to create " + request.getNumberOfGroups() + " groups. Found: "
                            + persons.size());
        }

        List<Group> tempGroups = createEmptyGroupsForPreview(request);

        distributePersons(persons, tempGroups, request);

        List<GroupResponse> groupResponses = tempGroups.stream()
                .map(GroupMapper::toDtoPreview)
                .collect(Collectors.toList());

        System.out.println("Preview generation completed successfully");
        return groupResponses;
    }

    public void savePreviewToDatabase(Draw savedDraw, List<GroupResponse> preview) {
        System.out.println("Saving preview groups to database for draw: " + savedDraw.getTitle());

        List<Group> groups = new ArrayList<>();

        for (GroupResponse groupResponse : preview) {
            Group group = new Group();
            group.setName(groupResponse.getName());
            group.setDraw(savedDraw);

            List<Person> persons = groupResponse.getPersons().stream()
                    .map(personResponse -> personRepository.findById(personResponse.getPersonId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Person not found: " + personResponse.getPersonId())))
                    .collect(Collectors.toList());

            group.setPersons(persons);
            groups.add(group);
        }

        savedDraw.setGroups(groups);
        groupRepository.saveAll(groups);

        System.out.println("Preview saved successfully to database");
    }

    private List<Group> createEmptyGroupsForPreview(GenerateGroupsRequest request) {
        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < request.getNumberOfGroups(); i++) {
            Group group = new Group();
            group.setName(request.getGroupNames().get(i));
            group.setDraw(null);
            group.setPersons(new ArrayList<>());
            groups.add(group);
        }
        return groups;
    }
}