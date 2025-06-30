package com.easygroup.controller;

import com.easygroup.entity.ListEntity;
import com.easygroup.entity.Person;
import com.easygroup.entity.User;
import com.easygroup.service.ListService;
import com.easygroup.service.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing persons associated with a list.
 */
@RestController
@RequestMapping("/api/lists/{listId}/persons")
public class PersonController {

    private final PersonService personService;
    private final ListService listService;

    @Autowired
    public PersonController(PersonService personService, ListService listService) {
        this.personService = personService;
        this.listService = listService;
    }

    /**
     * Get all persons belonging to a specific list.
     *
     * @param listId the ID of the list
     * @param user the authenticated user
     * @return the list of persons if the list is owned by the user, 403 otherwise
     */
    @GetMapping
    public ResponseEntity<List<Person>> getPersons(
            @PathVariable UUID listId,
            @AuthenticationPrincipal User user) {

        ListEntity list = listService.findByIdAndUserId(listId, user.getId());
        if (list == null) {
            return ResponseEntity.status(403).build();
        }

        List<Person> persons = personService.findByList(list);
        return ResponseEntity.ok(persons);
    }

    /**
     * Create a new person in a specific list.
     *
     * @param listId the ID of the list
     * @param person the person object to create
     * @param user the authenticated user
     * @return the created person, or 403 if the list is not owned by the user
     */
    @PostMapping
    public ResponseEntity<Person> createPerson(
            @PathVariable UUID listId,
            @RequestBody Person person,
            @AuthenticationPrincipal User user) {

        ListEntity list = listService.findByIdAndUserId(listId, user.getId());
        if (list == null) {
            return ResponseEntity.status(403).build();
        }

        person.setList(list);
        Person created = personService.save(person);
        return ResponseEntity.ok(created);
    }

    /**
     * Delete a person by ID from a specific list.
     *
     * @param listId the ID of the list
     * @param personId the ID of the person to delete
     * @param user the authenticated user
     * @return 204 No Content on success, 403 if not authorized
     */
    @DeleteMapping("/{personId}")
    public ResponseEntity<Void> deletePerson(
            @PathVariable UUID listId,
            @PathVariable UUID personId,
            @AuthenticationPrincipal User user) {

        ListEntity list = listService.findByIdAndUserId(listId, user.getId());
        if (list == null) {
            return ResponseEntity.status(403).build();
        }

        personService.deleteById(personId);
        return ResponseEntity.noContent().build();
    }
}
