package com.easygroup.repository;

import com.easygroup.entity.Draw;
import com.easygroup.entity.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for Draw entity.
 */
@Repository
public interface DrawRepository extends JpaRepository<Draw, UUID> {

    /**
     * Find all draws for a specific list.
     * 
     * @param list the list for which draws were created
     * @return a list of Draw objects
     */
    java.util.List<Draw> findByList(List list);

    /**
     * Find all draws for a specific list ordered by creation date (newest first).
     * 
     * @param list the list for which draws were created
     * @return a list of Draw objects ordered by creation date
     */
    java.util.List<Draw> findByListOrderByCreatedAtDesc(List list);

    /**
     * Count the number of draws for a specific list.
     * 
     * @param list the list for which draws were created
     * @return the number of draws for the list
     */
    long countByList(List list);
}
