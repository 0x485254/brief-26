package com.easygroup.service;

import com.easygroup.entity.ListEntity;
import com.easygroup.entity.ListShare;
import com.easygroup.entity.User;
import com.easygroup.repository.ListRepository;
import com.easygroup.repository.ListShareRepository;
import org.springframework.stereotype.Service;

@Service
public class ListShareService {

    private final ListRepository listRepository;
    private final ListShareRepository listShareRepository;

    public ListShareService(ListRepository listRepository, ListShareRepository listShareRepository) {
        this.listRepository = listRepository;
        this.listShareRepository = listShareRepository;
    }

    /**
     * Share a list with a user.
     *
     * @param list the list to share
     * @param userSharedTo the user to share with
     * @return the created ListShare
     */
    public ListShare shareList(ListEntity list, User userSharedTo) {
        list.setIsShared(true);
        listRepository.save(list);

        ListShare listShare = new ListShare();
        listShare.setList(list);
        listShare.setSharedWithUser(userSharedTo);
        return listShareRepository.save(listShare);
    }

    /**
     * Unshare a list with a user.
     *
     * @param list the list to unshare
     * @param user the user to unshare with
     */
    public ListEntity unshareList(ListEntity list, User user) {
        listShareRepository.findByListAndSharedWithUser(list, user)
                .ifPresent(listShareRepository::delete);

        // If no more shares exist, update the list's shared status
        if (listShareRepository.findByList(list).isEmpty()) {
            list.setIsShared(false);
            listRepository.save(list);
        }

        return list;
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
