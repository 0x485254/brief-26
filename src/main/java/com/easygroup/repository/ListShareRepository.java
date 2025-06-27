package com.easygroup.repository;

import com.easygroup.entity.List;
import com.easygroup.entity.ListShare;
import com.easygroup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ListShare entity.
 */
@Repository
public interface ListShareRepository extends JpaRepository<ListShare, UUID> {

    /**
     * Find all list shares for a specific user.
     * 
     * @param sharedWithUser the user who has access to the shared lists
     * @return a list of ListShare objects
     */
    java.util.List<ListShare> findBySharedWithUser(User sharedWithUser);

    /**
     * Find all list shares for a specific list.
     * 
     * @param list the list that is shared
     * @return a list of ListShare objects
     */
    java.util.List<ListShare> findByList(List list);

    /**
     * Find a list share by list and user.
     * 
     * @param list the list that is shared
     * @param sharedWithUser the user who has access to the shared list
     * @return an Optional containing the ListShare if found
     */
    Optional<ListShare> findByListAndSharedWithUser(List list, User sharedWithUser);

    /**
     * Check if a list is shared with a specific user.
     * 
     * @param list the list to check
     * @param sharedWithUser the user to check
     * @return true if the list is shared with the user
     */
    boolean existsByListAndSharedWithUser(List list, User sharedWithUser);
}
