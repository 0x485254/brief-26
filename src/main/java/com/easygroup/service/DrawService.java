package com.easygroup.service;

import com.easygroup.dto.DrawResponse;
import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.entity.Draw;
import com.easygroup.entity.ListEntity;
import com.easygroup.repository.DrawRepository;
import com.easygroup.repository.ListRepository;
import com.easygroup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private UserRepository userRepository;

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private GroupGenerationService groupGenerationService;
    /**
     * Create a draw and generate groups for the provided list.
     *
     * @param request parameters for group generation
     * @param userId  owner of the list
     * @param listId  identifier of the list
     * @return details of the created draw
     */
    public DrawResponse generateGroups(GenerateGroupsRequest request, UUID userId, UUID listId) {
        ListEntity list = validateUserListAccess(userId, listId);

        Draw draw = convertDtoToEntity(request, list);
        Draw savedDraw = drawRepository.save(draw);
        groupGenerationService.generateGroups(savedDraw, request);
        return convertEntityToDto(savedDraw);
    }

    /**
     * Retrieve all draws for a given list.
     */
    public java.util.List<DrawResponse> getDrawsForList(UUID userId, UUID listId) {
        ListEntity list = validateUserListAccess(userId, listId);
        java.util.List<Draw> draws = drawRepository.findByListOrderByCreatedAtDesc(list);
        return draws.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Ensure the given user has access to the target list.
     */
    private ListEntity validateUserListAccess(UUID userId, UUID listId) {
        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "List not found with id: " + listId));

        //to be added && !hasSharedAccess(userId, listId)
        if (!list.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to list: " + listId);
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
    /**
     * Map input request to Draw entity instance.
     */
    private Draw convertDtoToEntity(GenerateGroupsRequest request, ListEntity list) {
        Draw draw = new Draw();
        draw.setTitle(generateTitle(request.getTitle(), list.getName()));
        draw.setList(list);
        draw.setCreatedAt(LocalDateTime.now());
        return draw;
    }
    /**
     * Map Draw entity to its DTO representation.
     */
    private DrawResponse convertEntityToDto(Draw draw) {
        return DrawResponse.builder()
                .id(draw.getId())
                .title(draw.getTitle())
                .createdAt(draw.getCreatedAt())
                .listId(draw.getList().getId())
                .listName(draw.getList().getName())
                .groupCount(draw.getGroups() != null ? draw.getGroups().size() : 0)
                .build();
    }
}