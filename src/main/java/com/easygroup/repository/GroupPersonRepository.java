package com.easygroup.repository;

import com.easygroup.entity.Group;
import com.easygroup.entity.GroupPerson;
import com.easygroup.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for GroupPerson entity.
 */
@Repository
public interface GroupPersonRepository extends JpaRepository<GroupPerson, GroupPerson.GroupPersonId> {

    /**
     * Find all group-person associations for a specific group.
     * 
     * @param group the group to find associations for
     * @return a list of GroupPerson objects
     */
    List<GroupPerson> findByGroup(Group group);

    /**
     * Find all group-person associations for a specific person.
     * 
     * @param person the person to find associations for
     * @return a list of GroupPerson objects
     */
    List<GroupPerson> findByPerson(Person person);

    /**
     * Find a group-person association by group and person.
     * 
     * @param group the group to find the association for
     * @param person the person to find the association for
     * @return an Optional containing the GroupPerson if found
     */
    Optional<GroupPerson> findByGroupAndPerson(Group group, Person person);

    /**
     * Delete all group-person associations for a specific group.
     * 
     * @param group the group to delete associations for
     */
    void deleteByGroup(Group group);

    /**
     * Count the number of persons in a specific group.
     * 
     * @param group the group to count persons for
     * @return the number of persons in the group
     */
    long countByGroup(Group group);
}
