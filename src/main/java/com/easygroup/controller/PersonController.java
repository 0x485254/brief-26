package com.easygroup.controller;

import com.easygroup.dto.PersonRequest;
import com.easygroup.dto.PersonResponse;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.Person;
import com.easygroup.entity.User;
import com.easygroup.mapper.PersonMapper;
import com.easygroup.service.ListService;
import com.easygroup.service.PersonService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;


/**
 * REST controller for managing persons associated with a list.
 */
@RestController
@RequestMapping("/api/lists/{listId}/persons")
public class PersonController {

    private final PersonService personService;
    private final ListService listService;

    public PersonController(PersonService personService, ListService listService) {
        this.personService = personService;
        this.listService = listService;
    }

    /**
     * Get all persons belonging to a specific list.
     *
     * @param listId the ID of the list
     * @param user   the authenticated user
     * @return the list of persons if the list is owned by the user, 403 otherwise
     */
@GetMapping
public ResponseEntity<List<PersonResponse>> getPersons(
        @PathVariable UUID listId,
        @AuthenticationPrincipal User user) {

    ListEntity list = listService.findByIdAndUserId(listId, user.getId());
    if (list == null) {
        return ResponseEntity.status(403).build();
    }

    List<Person> persons = personService.findByList(list);
    List<PersonResponse> response = persons.stream()
            .map(PersonMapper::toDto)
            .toList();

    return ResponseEntity.ok(response);
}


    /**
     * Create a new person in a specific list.
     *
     * @param personRequest the person object to edit
     * @param user   the authenticated user
     * @param personId   new version of person
     * @return the created person, or 403 if the list is not owned by the user
     */
    @PutMapping("/{personId}")
    public ResponseEntity<PersonResponse> createPerson(
            @RequestBody @Valid PersonRequest personRequest,
            @AuthenticationPrincipal User user,
            @PathVariable UUID personId) {
        
        // Set the personId from path variable to the request
        Person person = new Person();
        person.setId(personId);
        person.setName(personRequest.getName());
        person.setGender(personRequest.getGender());
        person.setAge(personRequest.getAge());
        person.setFrenchLevel(personRequest.getFrenchLevel());
        person.setOldDwwm(personRequest.getOldDwwm());
        person.setTechLevel(personRequest.getTechLevel());
        person.setProfile(personRequest.getProfile());
        
        // Create the person using the service, assuming there's a method like this
        Person createdPerson = personService.edit(person);
        PersonResponse response = PersonMapper.toDto(createdPerson);
        
        // Return created status (201) with the created person in the body
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Create a new person in a specific list.
     *
     * @param listId the ID of the list
     * @param person the person object to create
     * @param user   the authenticated user
     * @return the created person, or 403 if the list is not owned by the user
     */
    @PostMapping
    public ResponseEntity<PersonResponse> updatePerson(
            @PathVariable UUID listId,
            @RequestBody @Valid PersonRequest personRequest,
            @AuthenticationPrincipal User user) {

        ListEntity list = listService.findByIdAndUserId(listId, user.getId());
        if (list == null) {
            return ResponseEntity.status(403).build();
        }

        Person person = new Person();
        person.setName(personRequest.getName());
        person.setGender(personRequest.getGender());
        person.setAge(personRequest.getAge());
        person.setFrenchLevel(personRequest.getFrenchLevel());
        person.setOldDwwm(personRequest.getOldDwwm());
        person.setTechLevel(personRequest.getTechLevel());
        person.setProfile(personRequest.getProfile());
        person.setList(list);

        Person created = personService.save(person);
        return ResponseEntity.ok(PersonMapper.toDto(created));
    }

    /**
     * Delete a person by ID from a specific list.
     *
     * @param listId   the ID of the list
     * @param personId the ID of the person to delete
     * @param user     the authenticated user
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