package com.easygroup.service;

import com.easygroup.entity.ListEntity;
import com.easygroup.entity.Person;
import com.easygroup.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing persons.
 */
@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Find all persons.
     *
     * @return a list of all persons
     */
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    /**
     * Find a person by ID.
     *
     * @param id the person ID
     * @return an Optional containing the person if found
     */
    public Optional<Person> findById(UUID id) {
        return personRepository.findById(id);
    }

    /**
     * Find all persons in a list.
     *
     * @param list the list containing the persons
     * @return a list of persons in the list
     */
    public List<Person> findByList(ListEntity list) {
        return personRepository.findByList(list);
    }

    /**
     * Find all persons in a list ordered by name.
     *
     * @param list the list containing the persons
     * @return a list of persons in the list ordered by name
     */
    public List<Person> findByListOrderByName(ListEntity list) {
        return personRepository.findByListOrderByNameAsc(list);
    }

    /**
     * Save a person.
     *
     * @param person the person to save
     * @return the saved person
     */
    public Person save(Person person) {
        return personRepository.save(person);
    }

    /**
     * Delete a person by ID.
     *
     * @param id the person ID
     */
    public void deleteById(UUID id) {
        personRepository.deleteById(id);
    }

    /**
     * Count the number of persons in a list.
     *
     * @param list the list containing the persons
     * @return the number of persons in the list
     */
    public long countByList(ListEntity list) {
        return personRepository.countByList(list);
    }

    /**
     * Delete all persons in a list.
     *
     * @param list the list containing the persons to delete
     */
    public void deleteByList(ListEntity list) {
        personRepository.deleteByList(list);
    }
}
