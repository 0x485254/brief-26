package com.easygroup.service;

import com.easygroup.entity.List;
import com.easygroup.entity.Person;
import com.easygroup.repository.ListRepository;
import com.easygroup.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PersonService(PersonRepository personRepository, ListRepository listRespository) {
        this.personRepository = personRepository;
        this.listRepository = listRespository;
    }

    /**
     * Find all persons.
     *
     * @return a list of all persons
     */
    public java.util.List<Person> findAll() {
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
    public java.util.List<Person> findByList(List list) {

        return personRepository.findByList(list);
    }

    /**
     * Find all persons in a list ordered by name.
     *
     * @param listId the list's UUID containing the persons
     * @return a list of persons in the list ordered by name
     */
    public java.util.List<Person> findByListIdOrderByName(UUID listId) {
        List list = listRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("List not found with id: " + listId));

        return personRepository.findByListOrderByNameAsc(list);
    }

    /**
     * Save a person.
     *
     * @param age         the age of the person
     * @param frenchLevel the French language proficiency level
     * @param gender      the gender of the person
     * @param name        the name of the person
     * @param oldDWWM     indicates if the person is from old DWWM
     * @param profile     the profile of the person
     * @param techLevel   the technical proficiency level
     * @param listId      the UUID of the associated list
     * @return the saved person
     */
    public Person save(Integer age, Integer frenchLevel, Person.Gender gender, String name, Boolean oldDWWM, Person.Profile profile, Integer techLevel, UUID listId){

        List list = listRepository.findById(listId)
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
     * Updates an existing person's attributes and saves the changes to the repository.
     *
     * @param id          the unique identifier of the person to edit
     * @param age         the updated age of the person
     * @param frenchLevel the updated French language proficiency level
     * @param gender      the updated gender of the person
     * @param name        the updated name of the person
     * @param oldDWWM     indicates whether the person belongs to an old DWWM group
     * @param profile     the updated profile of the person
     * @param techLevel   the updated technical proficiency level of the person
     * @return the updated and saved person instance
     * @throws IllegalArgumentException if no person is found with the provided id
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

        return personRepository.save(person);  // Using save instead of update
    }

    /**
     * Delete a person by ID.
     *
     * @param id the person ID
     */
    public void deleteById(UUID id) {
        personRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + id));

        personRepository.deleteById(id);
    }

    /**
     * Count the number of persons in a list.
     *
     * @param list the list containing the persons
     * @return the number of persons in the list
     */
    public long countByList(List list) {
        return personRepository.countByList(list);
    }

    /**
     * Delete all persons in a list.
     *
     * @param list the list containing the persons to delete
     */
    public void deleteByList(List list) {
        personRepository.deleteByList(list);
    }
}