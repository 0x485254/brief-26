package com.easygroup.service;

import com.easygroup.dto.DrawResponse;
import com.easygroup.dto.GenerateGroupsRequest;
import com.easygroup.entity.Draw;
import com.easygroup.entity.List;
import com.easygroup.entity.User;
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
public class DrawService {

    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private GroupGenerationService groupGenerationService;
    public DrawResponse generateGroups(GenerateGroupsRequest request, UUID userId, UUID listId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));

        List list = validateUserListAccess(userId, listId);

        Draw draw = convertDtoToEntity(request, list);
        Draw savedDraw = drawRepository.save(draw);
        groupGenerationService.generateGroups(savedDraw, request);
        return convertEntityToDto(savedDraw);
    }

    public java.util.List<DrawResponse> getDrawsForList(UUID userId, UUID listId) {
        List list = validateUserListAccess(userId, listId);
        java.util.List<Draw> draws = drawRepository.findByListOrderByCreatedAtDesc(list);
        return draws.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    private List validateUserListAccess(UUID userId, UUID listId) {
        List list = listRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "List not found with id: " + listId));

        //to be added && !hasSharedAccess(userId, listId)
        if (!list.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to list: " + listId);
        }

        return list;
    }

    private String generateTitle(String requestTitle, String listName) {
        if (requestTitle != null && !requestTitle.trim().isEmpty()) {
            return requestTitle.trim();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return "Groups for " + listName + " - " + timestamp;
    }
    private Draw convertDtoToEntity(GenerateGroupsRequest request, List list) {
        Draw draw = new Draw();
        draw.setTitle(generateTitle(request.getTitle(), list.getName()));
        draw.setList(list);
        draw.setCreatedAt(LocalDateTime.now());
        return draw;
    }
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