package com.easygroup.service;

import com.easygroup.dto.GroupResponse;
import com.easygroup.dto.PersonResponse;
import com.easygroup.entity.Draw;
import com.easygroup.entity.Group;
import com.easygroup.entity.Person;
import com.easygroup.repository.DrawRepository;
import com.easygroup.repository.GroupRepository;
import com.easygroup.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private PersonRepository personRepository;

    public Group createGroup(Group group, UUID drawId) {
        Optional<Draw> draw = drawRepository.findById(drawId);
        if (draw.isPresent()) {
            group.setDraw(draw.get());
            return groupRepository.save(group);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Draw not found with id: " + drawId);
    }

    public Optional<Group> getGroupById(UUID id) {
        return groupRepository.findById(id);
    }

    public List<GroupResponse> getGroupsByDrawId(UUID drawId) {
        Optional<Draw> draw = drawRepository.findById(drawId);
        if (draw.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Draw not found with id: " + drawId);
        }

        List<Group> groups = groupRepository.findByDrawOrderByNameAsc(draw.get());
        return groups.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group updateGroup(UUID id, Group group) {
        Optional<Group> existingGroup = groupRepository.findById(id);
        if (existingGroup.isPresent()) {
            Group groupToUpdate = existingGroup.get();
            groupToUpdate.setName(group.getName());
            return groupRepository.save(groupToUpdate);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found with id: " + id);
    }

    public boolean deleteGroup(UUID id) {
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void addPersonToGroup(UUID groupId, UUID personId) {
        Optional<Group> group = groupRepository.findById(groupId);
        Optional<Person> person = personRepository.findById(personId);

        if (group.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }
        if (person.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found");
        }

        Group groupToUpdate = group.get();
        Person personToAdd = person.get();

        boolean alreadyExists = groupToUpdate.getPersons().stream()
                .anyMatch(p -> p.getId().equals(personId));

        if (alreadyExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Person already in group");
        }

        groupToUpdate.getPersons().add(personToAdd);
        groupRepository.save(groupToUpdate);
    }

    public void removePersonFromGroup(UUID groupId, UUID personId) {
        Optional<Group> group = groupRepository.findById(groupId);

        if (group.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }

        Group groupToUpdate = group.get();

        long remainingCount = groupToUpdate.getPersons().stream()
                .filter(p -> !p.getId().equals(personId))
                .count();

        if (remainingCount == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot remove last person from group");
        }

        groupToUpdate.getPersons().removeIf(p -> p.getId().equals(personId));
        groupRepository.save(groupToUpdate);
    }

    private GroupResponse convertEntityToDto(Group group) {
        List<PersonResponse> personResponses = group.getPersons().stream()
                .map(this::convertPersonToDto)
                .collect(Collectors.toList());

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .drawId(group.getDraw().getId())
                .persons(personResponses)
                .personCount(personResponses.size())
                .build();
    }

    private PersonResponse convertPersonToDto(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getName(),
                person.getGender().toString(),
                person.getAge(),
                person.getFrenchLevel(),
                person.getOldDwwm(),
                person.getTechLevel(),
                person.getProfile().toString()
        );
    }
}