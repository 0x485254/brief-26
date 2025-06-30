package com.easygroup.service;

import com.easygroup.entity.ListEntity;
import com.easygroup.entity.Person;
import com.easygroup.repository.ListRepository;
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
    private final ListRepository listRepository;

    @Autowired
    public PersonService(PersonRepository personRepository, ListRepository listRepository) {
        this.personRepository = personRepository;
        this.listRepository = listRepository;
    }

    /**
     * Retrieve all persons in the database.
     *
     * @return list of all persons
     */
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    /**
     * Find a person by their UUID.
     *
     * @param id UUID of the person
     * @return Optional containing the person if found
     */
    public Optional<Person> findById(UUID id) {
        return personRepository.findById(id);
    }

    /**
     * Retrieve all persons belonging to a given list.
     *
     * @param list the list entity
     * @return list of persons in the list
     */
    public List<Person> findByList(ListEntity list) {
        return personRepository.findByList(list);
    }

    /**
     * Retrieve all persons in a list, sorted by name.
     *
     * @param listId UUID of the list
     * @return list of persons ordered by name
     * @throws IllegalArgumentException if list not found
     */
    public List<Person> findByListIdOrderByName(UUID listId) {
        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("List not found with id: " + listId));
        return personRepository.findByListOrderByNameAsc(list);
    }

    /**
     * Save a new person to the database.
     *
     * @param person the person to save
     * @return the saved person
     */
    public Person save(Person person) {
        return personRepository.save(person);
    }

    /**
     * Save a new person using individual attributes.
     *
     * @param age         age of the person
     * @param frenchLevel French level
     * @param gender      gender of the person
     * @param name        name of the person
     * @param oldDWWM     true if from old DWWM
     * @param profile     profile enum
     * @param techLevel   technical level
     * @param listId      UUID of the list
     * @return the saved person
     * @throws IllegalArgumentException if list not found
     */
    public Person save(Integer age, Integer frenchLevel, Person.Gender gender, String name,
                       Boolean oldDWWM, Person.Profile profile, Integer techLevel, UUID listId) {

        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("List not found with id: " + listId));

        Person person = new Person();
        person.setAge(age);
        person.setFrenchLevel(frenchLevel);
        person.setGender(gender);
        person.setName(name);
        person.setOldDwwm(oldDWWM);
        person.setProfile(profile);
        person.setTechLevel(techLevel);
        person.setList(list);

        return personRepository.save(person);
    }

    /**
     * Update an existing person.
     *
     * @param id          UUID of the person to edit
     * @param age         updated age
     * @param frenchLevel updated French level
     * @param gender      updated gender
     * @param name        updated name
     * @param oldDWWM     updated DWWM status
     * @param profile     updated profile
     * @param techLevel   updated technical level
     * @return the updated person
     * @throws IllegalArgumentException if person not found
     */
    public Person edit(UUID id, Integer age, Integer frenchLevel, Person.Gender gender,
                       String name, Boolean oldDWWM, Person.Profile profile, Integer techLevel) {

        Person person = personRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + id));

        person.setAge(age);
        person.setFrenchLevel(frenchLevel);
        person.setGender(gender);
        person.setName(name);
        person.setOldDwwm(oldDWWM);
        person.setProfile(profile);
        person.setTechLevel(techLevel);

        return personRepository.save(person);
    }

    /**
     * Delete a person by UUID.
     *
     * @param id the UUID of the person
     * @throws IllegalArgumentException if person not found
     */
    public void deleteById(UUID id) {
        personRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + id));
        personRepository.deleteById(id);
    }

    /**
     * Count the number of persons in a list.
     *
     * @param list the list
     * @return number of persons in the list
     */
    public long countByList(ListEntity list) {
        return personRepository.countByList(list);
    }

    /**
     * Delete all persons from a given list.
     *
     * @param list the list from which to delete persons
     */
    public void deleteByList(ListEntity list) {
        personRepository.deleteByList(list);
    }

    


}
