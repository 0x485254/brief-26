package com.easygroup.service;

import com.easygroup.entity.List;
import com.easygroup.entity.ListShare;
import com.easygroup.entity.User;
import com.easygroup.repository.ListRepository;
import com.easygroup.repository.ListShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ListService(ListRepository listRepository, ListShareRepository listShareRepository) {
        this.listRepository = listRepository;
        this.listShareRepository = listShareRepository;
    }

    /**
     * Find all lists.
     *
     * @return a list of all lists
     */
    public java.util.List<List> findAll() {
        return listRepository.findAll();
    }

    /**
     * Find a list by ID.
     *
     * @param id the list ID
     * @return an Optional containing the list if found
     */
    public Optional<List> findById(UUID id) {
        return listRepository.findById(id);
    }

    /**
     * Find all lists owned by a user.
     *
     * @param user the user who owns the lists
     * @return a list of lists owned by the user
     */
    public java.util.List<List> findByUser(User user) {
        return listRepository.findByUser(user);
    }

    /**
     * Find all lists shared with a user.
     *
     * @param user the user who has access to the shared lists
     * @return a list of lists shared with the user
     */
    public java.util.List<List> findSharedWithUser(User user) {
        return listShareRepository.findBySharedWithUser(user)
                .stream()
                .map(ListShare::getList)
                .collect(Collectors.toList());
    }

    /**
     * Save a list.
     *
     * @param listName the list name
     * @param user the user who owns the list
     * @return the saved list
     */
    public List save(String listName, User user) {
        if (findByUser(user).stream().anyMatch(l -> l.getName().equals(listName))){
            throw new IllegalArgumentException("List name already exists for user: " + user.getEmail());
        }

        List list = new List();
        list.setName(listName);

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
}
