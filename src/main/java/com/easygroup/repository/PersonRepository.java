package com.easygroup.repository;

import com.easygroup.entity.List;
import com.easygroup.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for Person entity.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {

    /**
     * Find all persons in a specific list.
     * 
     * @param list the list containing the persons
     * @return a list of Person objects
     */
    java.util.List<Person> findByList(List list);

    /**
     * Find all persons in a specific list ordered by name.
     * 
     * @param list the list containing the persons
     * @return a list of Person objects ordered by name
     */
    java.util.List<Person> findByListOrderByNameAsc(List list);

    /**
     * Count the number of persons in a specific list.
     * 
     * @param list the list containing the persons
     * @return the number of persons in the list
     */
    long countByList(List list);

    /**
     * Delete all persons in a specific list.
     * 
     * @param list the list containing the persons to delete
     */
    void deleteByList(List list);
}
