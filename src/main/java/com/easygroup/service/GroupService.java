package com.easygroup.service;

import com.easygroup.entity.Draw;
import com.easygroup.entity.Group;
import com.easygroup.entity.GroupPerson;
import com.easygroup.entity.Person;
import com.easygroup.repository.GroupPersonRepository;
import com.easygroup.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing groups.
 */
@Service
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupPersonRepository groupPersonRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, GroupPersonRepository groupPersonRepository) {
        this.groupRepository = groupRepository;
        this.groupPersonRepository = groupPersonRepository;
    }

    /**
     * Find all groups.
     *
     * @return a list of all groups
     */
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    /**
     * Find a group by ID.
     *
     * @param id the group ID
     * @return an Optional containing the group if found
     */
    public Optional<Group> findById(Integer id) {
        return groupRepository.findById(id);
    }

    /**
     * Find all groups for a draw.
     *
     * @param draw the draw for which groups were created
     * @return a list of groups for the draw
     */
    public List<Group> findByDraw(Draw draw) {
        return groupRepository.findByDraw(draw);
    }

    /**
     * Find all groups for a draw ordered by name.
     *
     * @param draw the draw for which groups were created
     * @return a list of groups for the draw ordered by name
     */
    public List<Group> findByDrawOrderByName(Draw draw) {
        return groupRepository.findByDrawOrderByNameAsc(draw);
    }

    /**
     * Save a group.
     *
     * @param group the group to save
     * @return the saved group
     */
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    /**
     * Delete a group by ID.
     *
     * @param id the group ID
     */
    public void deleteById(Integer id) {
        groupRepository.deleteById(id);
    }

    /**
     * Add a person to a group.
     *
     * @param group the group to add the person to
     * @param person the person to add to the group
     * @return the created GroupPerson
     */
    public GroupPerson addPersonToGroup(Group group, Person person) {
        GroupPerson groupPerson = new GroupPerson();
        groupPerson.setGroup(group);
        groupPerson.setPerson(person);
        return groupPersonRepository.save(groupPerson);
    }

    /**
     * Remove a person from a group.
     *
     * @param group the group to remove the person from
     * @param person the person to remove from the group
     */
    public void removePersonFromGroup(Group group, Person person) {
        groupPersonRepository.findByGroupAndPerson(group, person)
                .ifPresent(groupPersonRepository::delete);
    }

    /**
     * Find all persons in a group.
     *
     * @param group the group to find persons for
     * @return a list of persons in the group
     */
    public List<Person> findPersonsByGroup(Group group) {
        return groupPersonRepository.findByGroup(group)
                .stream()
                .map(GroupPerson::getPerson)
                .collect(Collectors.toList());
    }

    /**
     * Count the number of persons in a group.
     *
     * @param group the group to count persons for
     * @return the number of persons in the group
     */
    public long countPersonsByGroup(Group group) {
        return groupPersonRepository.countByGroup(group);
    }

    /**
     * Count the number of groups for a draw.
     *
     * @param draw the draw to count groups for
     * @return the number of groups for the draw
     */
    public long countByDraw(Draw draw) {
        return groupRepository.countByDraw(draw);
    }
}