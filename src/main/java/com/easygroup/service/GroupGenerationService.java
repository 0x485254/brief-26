package com.easygroup.service;

import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.entity.Draw;
import com.easygroup.entity.Group;
import com.easygroup.entity.GroupPerson;
import com.easygroup.entity.Person;
import com.easygroup.repository.DrawRepository;
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
    private DrawRepository drawRepository;

    @Autowired
    private PersonRepository personRepository;

    public void generateGroups(Draw draw, GenerateGroupsRequest request) {
        System.out.println("Starting group generation for draw: " + draw.getTitle());
        System.out.println("Request: " + request);

        List<Person> persons = personRepository.findByList(draw.getList());
        System.out.println("Found " + persons.size() + " persons in list: " + draw.getList().getName());

        if (persons.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create groups: No persons found in the list");
        }

        if (persons.size() < request.getNumberOfGroups()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Not enough persons to create " + request.getNumberOfGroups() + " groups. Found: " + persons.size());
        }

        List<Group> groups = createEmptyGroups(request, draw);
        initialDistribution(persons, groups);
        applySelectedCriteria(groups, request);
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
            group.setGroupPersons(new ArrayList<>());
            groups.add(group);
        }

        return groups;
    }

    private void initialDistribution(List<Person> persons, List<Group> groups) {
        System.out.println("Starting initial random distribution");

        Collections.shuffle(persons);
        System.out.println("Shuffled all persons randomly");

        for (int i = 0; i < persons.size(); i++) {
            Group targetGroup = groups.get(i % groups.size());

            GroupPerson groupPerson = new GroupPerson();
            groupPerson.setGroup(targetGroup);
            groupPerson.setPerson(persons.get(i));
            targetGroup.getGroupPersons().add(groupPerson);

            System.out.println(persons.get(i).getName() + " assigned to " + targetGroup.getName());
        }
    }

    private void applySelectedCriteria(List<Group> groups, GenerateGroupsRequest request) {
        System.out.println("Applying selected balancing criteria");

        if (request.getBalanceByGender()) {
            System.out.println("Applying gender rebalancing");
            rebalanceByGender(groups);
        }

        if (request.getBalanceByTechLevel()) {
            System.out.println("Applying tech level rebalancing");
            rebalanceByTechLevel(groups);
        }

        if (request.getBalanceByAge()) {
            System.out.println("Applying age rebalancing");
            rebalanceByAge(groups);
        }

        if (request.getBalanceByOldDwwm()) {
            System.out.println("Applying old DWWM rebalancing");
            rebalanceByOldDwwm(groups);
        }

        if (request.getBalanceByFrenchLevel()) {
            System.out.println("Applying French level rebalancing");
            rebalanceByFrenchLevel(groups);
        }

        if (request.getBalanceByProfile()) {
            System.out.println("Applying personality profile rebalancing");
            rebalanceByProfile(groups);
        }
    }

    private void rebalanceByGender(List<Group> groups) {
        List<Person> allPersons = extractAllPersons(groups);
        clearAllGroups(groups);

        Map<Person.Gender, List<Person>> genderGroups = allPersons.stream()
                .collect(Collectors.groupingBy(Person::getGender));

        System.out.println("Grouped by gender:");
        for (Map.Entry<Person.Gender, List<Person>> entry : genderGroups.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().size() + " persons");
        }

        for (Map.Entry<Person.Gender, List<Person>> entry : genderGroups.entrySet()) {
            List<Person> genderPersons = entry.getValue();
            Collections.shuffle(genderPersons);
            distributeRoundRobin(genderPersons, groups);
        }
    }

    private void rebalanceByTechLevel(List<Group> groups) {
        List<Person> allPersons = extractAllPersons(groups);
        clearAllGroups(groups);

        Map<Integer, List<Person>> techGroups = allPersons.stream()
                .collect(Collectors.groupingBy(Person::getTechLevel));

        System.out.println("Grouped by tech level:");
        for (Map.Entry<Integer, List<Person>> entry : techGroups.entrySet()) {
            System.out.println("Level " + entry.getKey() + ": " + entry.getValue().size() + " persons");
        }

        for (Map.Entry<Integer, List<Person>> entry : techGroups.entrySet()) {
            List<Person> techPersons = entry.getValue();
            Collections.shuffle(techPersons);
            distributeRoundRobin(techPersons, groups);
        }
    }

    private void rebalanceByAge(List<Group> groups) {
        List<Person> allPersons = extractAllPersons(groups);
        clearAllGroups(groups);

        allPersons.sort(Comparator.comparing(Person::getAge));
        System.out.println("Sorted by age for balanced distribution");

        distributeRoundRobin(allPersons, groups);
    }

    private void rebalanceByOldDwwm(List<Group> groups) {
        List<Person> allPersons = extractAllPersons(groups);
        clearAllGroups(groups);

        Map<Boolean, List<Person>> dwwmGroups = allPersons.stream()
                .collect(Collectors.groupingBy(Person::getOldDwwm));

        System.out.println("Grouped by old DWWM:");
        for (Map.Entry<Boolean, List<Person>> entry : dwwmGroups.entrySet()) {
            String label = entry.getKey() ? "Former DWWM" : "Not former DWWM";
            System.out.println(label + ": " + entry.getValue().size() + " persons");
        }

        for (Map.Entry<Boolean, List<Person>> entry : dwwmGroups.entrySet()) {
            List<Person> dwwmPersons = entry.getValue();
            Collections.shuffle(dwwmPersons);
            distributeRoundRobin(dwwmPersons, groups);
        }
    }

    private void rebalanceByFrenchLevel(List<Group> groups) {
        List<Person> allPersons = extractAllPersons(groups);
        clearAllGroups(groups);

        Map<Integer, List<Person>> frenchGroups = allPersons.stream()
                .collect(Collectors.groupingBy(Person::getFrenchLevel));

        System.out.println("Grouped by French level:");
        for (Map.Entry<Integer, List<Person>> entry : frenchGroups.entrySet()) {
            System.out.println("Level " + entry.getKey() + ": " + entry.getValue().size() + " persons");
        }

        for (Map.Entry<Integer, List<Person>> entry : frenchGroups.entrySet()) {
            List<Person> frenchPersons = entry.getValue();
            Collections.shuffle(frenchPersons);
            distributeRoundRobin(frenchPersons, groups);
        }
    }

    private void rebalanceByProfile(List<Group> groups) {
        List<Person> allPersons = extractAllPersons(groups);
        clearAllGroups(groups);

        Map<Person.Profile, List<Person>> profileGroups = allPersons.stream()
                .collect(Collectors.groupingBy(Person::getProfile));

        System.out.println("Grouped by personality profile:");
        for (Map.Entry<Person.Profile, List<Person>> entry : profileGroups.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().size() + " persons");
        }

        for (Map.Entry<Person.Profile, List<Person>> entry : profileGroups.entrySet()) {
            List<Person> profilePersons = entry.getValue();
            Collections.shuffle(profilePersons);
            distributeRoundRobin(profilePersons, groups);
        }
    }

    private List<Person> extractAllPersons(List<Group> groups) {
        return groups.stream()
                .flatMap(group -> group.getGroupPersons().stream())
                .map(GroupPerson::getPerson)
                .collect(Collectors.toList());
    }

    private void clearAllGroups(List<Group> groups) {
        groups.forEach(group -> group.getGroupPersons().clear());
    }

    private void distributeRoundRobin(List<Person> persons, List<Group> groups) {
        for (int i = 0; i < persons.size(); i++) {
            Group targetGroup = groups.get(i % groups.size());

            GroupPerson groupPerson = new GroupPerson();
            groupPerson.setGroup(targetGroup);
            groupPerson.setPerson(persons.get(i));
            targetGroup.getGroupPersons().add(groupPerson);
        }
    }
}
