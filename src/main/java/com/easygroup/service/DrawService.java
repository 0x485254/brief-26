package com.easygroup.service;

import com.easygroup.dto.DrawResponse;
import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.dto.GroupPreviewResponse;
import com.easygroup.dto.GroupResponse;
import com.easygroup.entity.Draw;
import com.easygroup.entity.Group;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.Person;
import com.easygroup.mapper.DrawMapper;
import com.easygroup.repository.DrawRepository;
import com.easygroup.repository.GroupRepository;
import com.easygroup.repository.ListRepository;
import com.easygroup.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
/**
 * Service handling draw creation and retrieval.
 */
public class DrawService {

    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private GroupGenerationService groupGenerationService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private GroupRepository groupRepository;

    public GroupPreviewResponse generatePreview(GenerateGroupsRequest request, UUID userId, UUID listId) {
        ListEntity list = validateUserListAccess(userId, listId);

        List<GroupResponse> groups = groupGenerationService.generateGroupsPreview(list, request);

        return GroupPreviewResponse.builder()
                .listId(listId)
                .listName(list.getName())
                .title(generateTitle(request.getTitle(), list.getName()))
                .groups(groups)
                .groupCount(groups.size())
                .totalPersons(calculateTotalPersons(groups))
                .generatedAt(LocalDateTime.now())
                .build();
    }

    public DrawResponse saveModifiedGroups(GroupPreviewResponse modifiedPreview, UUID userId, UUID listId) {
        ListEntity list = validateUserListAccess(userId, listId);

        if (modifiedPreview.getGroups() == null || modifiedPreview.getGroups().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Groups list cannot be null or empty");
        }

        Draw draw = new Draw();
        draw.setTitle(modifiedPreview.getTitle());
        draw.setList(list);
        draw.setCreatedAt(LocalDateTime.now());
        Draw savedDraw = drawRepository.save(draw);

        List<Group> groups = new ArrayList<>();

        for (GroupResponse groupResponse : modifiedPreview.getGroups()) {
            Group group = new Group();
            group.setName(groupResponse.getName());
            group.setDraw(savedDraw);

            List<Person> persons = groupResponse.getPersons().stream()
                    .<Person>map(personResponse -> personRepository.findById(personResponse.getPersonId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Person not found: " + personResponse.getPersonId())))
                    .collect(Collectors.toList());

            group.setPersons(persons);
            groups.add(group);
        }

        groupRepository.saveAll(groups);
        savedDraw.setGroups(groups);

        return DrawMapper.toDto(savedDraw);
    }

    // public DrawResponse generateGroups(GenerateGroupsRequest request, UUID
    // userId, UUID listId) {
    // ListEntity list = validateUserListAccess(userId, listId);
    // Draw draw = convertDtoToEntity(request, list);
    // Draw savedDraw = drawRepository.save(draw);
    // groupGenerationService.generateGroups(savedDraw, request);

    // Draw reloadedDraw = drawRepository.findById(savedDraw.getId())
    // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Draw
    // not found after save"));
    // return DrawMapper.toDto(reloadedDraw);
    // }

    public List<DrawResponse> getDrawsForList(UUID userId, UUID listId) {
        ListEntity list = validateUserListAccess(userId, listId);
        List<Draw> draws = drawRepository.findByListOrderByCreatedAtDesc(list);

        return draws.stream()
                .map(DrawMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Ensure the given user has access to the target list.
     */
    private ListEntity validateUserListAccess(UUID userId, UUID listId) {
        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "List not found with id: " + listId));

        if (!list.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User does not have access to list: " + listId);
        }

        return list;
    }

    /**
     * Generate a default title when none is provided by the client.
     */
    private String generateTitle(String requestTitle, String listName) {
        if (requestTitle != null && !requestTitle.trim().isEmpty()) {
            return requestTitle.trim();
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return "Groups for " + listName + " - " + timestamp;
    }

    // private Draw convertDtoToEntity(GenerateGroupsRequest request, ListEntity
    // list) {
    // Draw draw = new Draw();
    // draw.setTitle(generateTitle(request.getTitle(), list.getName()));
    // draw.setList(list);
    // draw.setCreatedAt(LocalDateTime.now());
    // return draw;
    // }

    private int calculateTotalPersons(List<GroupResponse> groups) {
        return groups.stream()
                .mapToInt(GroupResponse::getPersonCount)
                .sum();
    }
}