package com.easygroup.repository;

import com.easygroup.entity.ListEntity;
import com.easygroup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ListEntity.
 */
@Repository
public interface ListRepository extends JpaRepository<ListEntity, Integer> {
    
    /**
     * Find all lists owned by a user.
     * 
     * @param user the user who owns the lists
     * @return a list of ListEntity objects
     */
    List<ListEntity> findByUser(User user);
    
    /**
     * Find a list by its name and owner.
     * 
     * @param name the name of the list
     * @param user the owner of the list
     * @return an Optional containing the list if found
     */
    Optional<ListEntity> findByNameAndUser(String name, User user);
    
    /**
     * Check if a list exists with the given name and owner.
     * 
     * @param name the name of the list
     * @param user the owner of the list
     * @return true if a list exists with the name and owner
     */
    boolean existsByNameAndUser(String name, User user);
}