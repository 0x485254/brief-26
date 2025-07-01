package com.easygroup.service;

import com.easygroup.entity.ListEntity;
import com.easygroup.entity.ListShare;
import com.easygroup.entity.User;
import com.easygroup.repository.ListRepository;
import com.easygroup.repository.ListShareRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing lists.
 */
@Service
@Transactional
public class ListService {

    private final ListRepository listRepository;
    private final ListShareRepository listShareRepository;

    public ListService(ListRepository listRepository, ListShareRepository listShareRepository) {
        this.listRepository = listRepository;
        this.listShareRepository = listShareRepository;
    }

    /**
     * Find all lists.
     *
     * @return a list of all lists
     */
    public java.util.List<ListEntity> findAll() {
        return listRepository.findAll();
    }

    /**
     * Find a list by ID.
     *
     * @param id the list ID
     * @return an Optional containing the list if found
     */
    public Optional<ListEntity> findById(UUID id) {
        return listRepository.findById(id);
    }

    /**
     * Find all lists owned by a user.
     *
     * @param user the user who owns the lists
     * @return a list of lists owned by the user
     */
    public java.util.List<ListEntity> findByUser(User user) {
        return listRepository.findByUser(user);
    }

    /**
     * Find all lists owned by a user using the user id.
     *
     * @param userId the id of the user who owns the lists
     * @return a list of lists owned by the user
     */
    public java.util.List<ListEntity> findByUserId(UUID userId) {
        return listRepository.findByUser_Id(userId);
    }

    /**
     * Find all lists shared with a user.
     *
     * @param user the user who has access to the shared lists
     * @return a list of lists shared with the user
     */
    public java.util.List<ListEntity> findSharedWithUser(User user) {
        return listShareRepository.findBySharedWithUser(user)
                .stream()
                .map(ListShare::getList)
                .collect(Collectors.toList());
    }

    /**
     * Save a list.
     *
     * @param listName the list name
     * @param user     the user who owns the list
     * @return the saved list
     */
    public ListEntity save(String listName, User user) {
        System.out.println("User inside save = " + user);

        if (findByUser(user).stream().anyMatch(l -> l.getName().equals(listName))) {
            throw new IllegalArgumentException("List name already exists for user: " + user.getEmail());
        }

        ListEntity list = new ListEntity();
        list.setName(listName);
        list.setUser(user); // 🔥 Obligatoire !

        return listRepository.save(list);
    }

    /**
     * Delete a list by ID.
     *
     * @param id the list ID
     */
    public void deleteById(UUID id) {
        listRepository.deleteById(id);
    }

public ListEntity findByIdAndUserId(UUID listId, UUID userId) {
    return listRepository.findByIdAndUser_Id(listId, userId).orElse(null);
}

}
