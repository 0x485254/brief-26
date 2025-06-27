package com.easygroup.controller;

import com.easygroup.dto.PersonRequest;
import com.easygroup.entity.Person;
import com.easygroup.service.PersonService;
import com.easygroup.dto.PersonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/lists/{listId}/persons")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<PersonResponse> addPersonToList(@PathVariable UUID userId, @PathVariable UUID listId, @RequestBody PersonRequest request){
        try{
            Person person = personService.save(request.getAge(), request.getFrenchLevel(), request.getGender(), request.getName(), request.getOldDwwm(), request.getProfile(), request.getTechLevel(), listId);

            PersonResponse response = new PersonResponse(
                    person.getId(),
                    person.getName(),
                    person.getGender().toString(),
                    person.getAge(),
                    person.getFrenchLevel(),
                    person.getOldDwwm(),
                    person.getTechLevel(),
                    person.getProfile().toString()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PersonResponse>> getPersonsByList(@PathVariable UUID userId, @PathVariable UUID listId) {
        try {
            List<Person> personList = personService.findByListIdOrderByName(listId);

            List<PersonResponse> responseList = personList.stream()
                    .map(person -> new PersonResponse(
                            person.getId(),
                            person.getName(),
                            person.getGender().toString(),
                            person.getAge(),
                            person.getFrenchLevel(),
                            person.getOldDwwm(),
                            person.getTechLevel(),
                            person.getProfile().toString()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{personId}")
    public ResponseEntity<PersonResponse> editPerson(@PathVariable UUID userId, @PathVariable UUID listId, @PathVariable UUID personId, @RequestBody PersonRequest request){
        try{
            Person person = personService.edit(personId, request.getAge(), request.getFrenchLevel(), request.getGender(), request.getName(), request.getOldDwwm(), request.getProfile(), request.getTechLevel());

            PersonResponse response = new PersonResponse(
                    person.getId(),
                    person.getName(),
                    person.getGender().toString(),
                    person.getAge(),
                    person.getFrenchLevel(),
                    person.getOldDwwm(),
                    person.getTechLevel(),
                    person.getProfile().toString()
            );

            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }


    @DeleteMapping("/{personId}")
    public ResponseEntity<Void> deletePerson(@PathVariable UUID userId, @PathVariable UUID listId, @PathVariable UUID personId) {
        try {
            personService.deleteById(personId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


}