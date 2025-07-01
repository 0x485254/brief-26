package com.easygroup.service;

import com.easygroup.dto.GroupResponse;
import com.easygroup.dto.PersonResponse;
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

    /**
     * Create a new group associated with a draw.
     *
     * @param group  group entity to persist
     * @param drawId identifier of the draw owning the group
     * @return saved group
     */
    public Group createGroup(Group group, UUID drawId) {
        Optional<Draw> draw = drawRepository.findById(drawId);
        if (draw.isPresent()) {
            group.setDraw(draw.get());
            return groupRepository.save(group);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Draw not found with id: " + drawId);
    }

    /**
     * Find a group by its id.
     */
    public Optional<Group> getGroupById(UUID id) {
        return groupRepository.findById(id);
    }

    /**
     * Retrieve all groups for a specific draw ordered by name.
     */
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

    /** Return every stored group. */
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * Update a group's name.
     */
    public Group updateGroup(UUID id, Group group) {
        Optional<Group> existingGroup = groupRepository.findById(id);
        if (existingGroup.isPresent()) {
            Group groupToUpdate = existingGroup.get();
            groupToUpdate.setName(group.getName());
            return groupRepository.save(groupToUpdate);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found with id: " + id);
    }

    /**
     * Delete a group by id.
     *
     * @return true if deletion occurred
     */
    public boolean deleteGroup(UUID id) {
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Manually add a person to a group.
     */
    public Group addPersonToGroup(UUID groupId, UUID personId) {
        Optional<Group> group = groupRepository.findById(groupId);
        Optional<Person> person = personRepository.findById(personId);

        if (group.isPresent() && person.isPresent()) {
            Group groupToUpdate = group.get();

            boolean alreadyExists = groupToUpdate.getGroupPersons().stream()
                    .anyMatch(gp -> gp.getPerson().getId().equals(personId));

            if (!alreadyExists) {
                GroupPerson groupPerson = new GroupPerson();
                groupPerson.setGroup(groupToUpdate);
                groupPerson.setPerson(person.get());
                groupToUpdate.getGroupPersons().add(groupPerson);
                return groupRepository.save(groupToUpdate);
            }
            return groupToUpdate;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group or Person not found");
    }

    /**
     * Manually remove a person from a group.
     */
    public Group removePersonFromGroup(UUID groupId, UUID personId) {
        Optional<Group> group = groupRepository.findById(groupId);

        if (group.isPresent()) {
            Group groupToUpdate = group.get();
            groupToUpdate.getGroupPersons().removeIf(gp -> gp.getPerson().getId().equals(personId));
            return groupRepository.save(groupToUpdate);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
    }

    private GroupResponse convertEntityToDto(Group group) {
        List<PersonResponse> personResponses = group.getGroupPersons().stream()
                .map(groupPerson -> convertPersonToDto(groupPerson.getPerson()))
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