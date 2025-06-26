package com.easygroup.service;

import com.easygroup.entity.ListEntity;
import com.easygroup.entity.ListShare;
import com.easygroup.entity.User;
import com.easygroup.repository.ListRepository;
import com.easygroup.repository.ListShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public List<ListEntity> findAll() {
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
    public List<ListEntity> findByUser(User user) {
        return listRepository.findByUser(user);
    }

    /**
     * Find all lists shared with a user.
     *
     * @param user the user who has access to the shared lists
     * @return a list of lists shared with the user
     */
    public List<ListEntity> findSharedWithUser(User user) {
        return listShareRepository.findBySharedWithUser(user)
                .stream()
                .map(ListShare::getList)
                .collect(Collectors.toList());
    }

    /**
     * Save a list.
     *
     * @param list the list to save
     * @return the saved list
     */
    public ListEntity save(ListEntity list) {
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

    /**
     * Share a list with a user.
     *
     * @param list the list to share
     * @param user the user to share with
     * @return the created ListShare
     */
    public ListShare shareList(ListEntity list, User user) {
        list.setIsShared(true);
        listRepository.save(list);

        ListShare listShare = new ListShare();
        listShare.setList(list);
        listShare.setSharedWithUser(user);
        return listShareRepository.save(listShare);
    }

    /**
     * Unshare a list with a user.
     *
     * @param list the list to unshare
     * @param user the user to unshare with
     */
    public void unshareList(ListEntity list, User user) {
        listShareRepository.findByListAndSharedWithUser(list, user)
                .ifPresent(listShareRepository::delete);

        // If no more shares exist, update the list's shared status
        if (listShareRepository.findByList(list).isEmpty()) {
            list.setIsShared(false);
            listRepository.save(list);
        }
    }

    /**
     * Check if a list is shared with a user.
     *
     * @param list the list to check
     * @param user the user to check
     * @return true if the list is shared with the user
     */
    public boolean isListSharedWithUser(ListEntity list, User user) {
        return listShareRepository.existsByListAndSharedWithUser(list, user);
    }
}
