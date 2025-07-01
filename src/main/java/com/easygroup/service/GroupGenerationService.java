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

        distributePersonsWithBalancing(persons, groups, request);
        draw.setGroups(groups);
        groupRepository.saveAll(groups);

        System.out.println("Group generation completed successfully");
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

    private void distributePersonsWithBalancing(List<Person> persons, List<Group> groups,
            GenerateGroupsRequest request) {
        System.out.println("Starting intelligent distribution with balancing");

        int totalPersons = persons.size();
        int numberOfGroups = groups.size();
        int baseSize = totalPersons / numberOfGroups;
        int remainder = totalPersons % numberOfGroups;

        int[] targetSizes = new int[numberOfGroups];
        for (int i = 0; i < numberOfGroups; i++) {
            targetSizes[i] = baseSize + (i < remainder ? 1 : 0);
        }

        System.out.println("Target group sizes: " + Arrays.toString(targetSizes));
        System.out.println("Total persons: " + totalPersons);

        List<Person> availablePersons = new ArrayList<>(persons);

        if (request.getBalanceByGender()) {
            System.out.println("Applying gender balancing");
            distributeByAttribute(availablePersons, groups, targetSizes, Person::getGender);
        }

        if (request.getBalanceByTechLevel()) {
            System.out.println("Applying tech level balancing");
            distributeByAttribute(availablePersons, groups, targetSizes, Person::getTechLevel);
        }

        if (request.getBalanceByFrenchLevel()) {
            System.out.println("Applying French level balancing");
            distributeByAttribute(availablePersons, groups, targetSizes, Person::getFrenchLevel);
        }

        if (request.getBalanceByOldDwwm()) {
            System.out.println("Applying old DWWM balancing");
            distributeByAttribute(availablePersons, groups, targetSizes, Person::getOldDwwm);
        }

        if (request.getBalanceByProfile()) {
            System.out.println("Applying personality profile balancing");
            distributeByAttribute(availablePersons, groups, targetSizes, Person::getProfile);
        }

        if (request.getBalanceByAge()) {
            System.out.println("Applying age balancing");
            distributeByAge(availablePersons, groups, targetSizes);
        }

        distributeRemainingPersons(availablePersons, groups, targetSizes);

        verifyDistribution(groups, targetSizes);
    }

    private <T> void distributeByAttribute(List<Person> availablePersons, List<Group> groups,
            int[] targetSizes, java.util.function.Function<Person, T> attributeGetter) {

        // unassignedPersons = [dodo, bobo, nono, mimi, lala] // All the people
        List<Person> unassignedPersons = availablePersons.stream()
                .filter(person -> groups.stream().noneMatch(group -> group.getPersons().contains(person)))
                .collect(Collectors.toList());

        if (unassignedPersons.isEmpty()) {
            return;
        }

        Map<T, List<Person>> attributeGroups = unassignedPersons.stream()
                .collect(Collectors.groupingBy(attributeGetter));

        // IT WILL LOOKS LIKE THIS: {MALE=[dodod, nono, popopo], FEMALE=[mimi, lala]}
        for (Map.Entry<T, List<Person>> entry : attributeGroups.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().size() + " persons");
        }

        List<Person> personsToRemove = new ArrayList<>();

        for (Map.Entry<T, List<Person>> entry : attributeGroups.entrySet()) {
            List<Person> personsWithAttribute = new ArrayList<>(entry.getValue());
            // hmmmmmm we well have something like this 'personsWithAttribute =
            // [dodod,nono, popopo]'
            Collections.shuffle(personsWithAttribute);
            // we will shuffle the persons with the same attribute to ensure randomness

            // find the persons with the same attribute and distribute them
            for (Person person : personsWithAttribute) {
                Group bestGroup = findBestGroupForPerson(groups, targetSizes, person, attributeGetter);
                if (bestGroup != null) {
                    bestGroup.getPersons().add(person);
                    personsToRemove.add(person);
                }
            }
        }

        availablePersons.removeAll(personsToRemove);
    }

    private void distributeByAge(List<Person> availablePersons, List<Group> groups, int[] targetSizes) {
        List<Person> unassignedPersons = availablePersons.stream()
                .filter(person -> groups.stream().noneMatch(group -> group.getPersons().contains(person)))
                .sorted(Comparator.comparing(Person::getAge))
                .collect(Collectors.toList());

        System.out.println("Distributing " + unassignedPersons.size() + " unassigned persons by age (sorted)");

        List<Person> personsToRemove = new ArrayList<>();

        for (Person person : unassignedPersons) {
            Group bestGroup = findBestGroupForPerson(groups, targetSizes, person, Person::getAge);
            if (bestGroup != null) {
                bestGroup.getPersons().add(person);
                personsToRemove.add(person);
            }
        }

        availablePersons.removeAll(personsToRemove);
    }

    private <T> Group findBestGroupForPerson(List<Group> groups, int[] targetSizes, Person person,
            java.util.function.Function<Person, T> attributeGetter) {

        T personAttribute = attributeGetter.apply(person);

        List<Group> availableGroups = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getPersons().size() < targetSizes[i]) {
                availableGroups.add(groups.get(i));
            }
        }

        if (availableGroups.isEmpty()) {
            return null;
        }

        return availableGroups.stream()
                .min(Comparator.<Group>comparingInt(group -> (int) group.getPersons().stream()
                        .filter(p -> attributeGetter.apply(p).equals(personAttribute))
                        .count())
                        .thenComparingInt(group -> group.getPersons().size()))
                .orElse(availableGroups.get(0));
    }

    private void distributeRemainingPersons(List<Person> availablePersons, List<Group> groups, int[] targetSizes) {
        System.out.println("Distributing " + availablePersons.size() + " remaining persons");

        Collections.shuffle(availablePersons);

        for (Person person : new ArrayList<>(availablePersons)) {
            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).getPersons().size() < targetSizes[i]) {
                    groups.get(i).getPersons().add(person);
                    availablePersons.remove(person);
                    break;
                }
            }
        }
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

        distributePersonsWithBalancing(persons, tempGroups, request);
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