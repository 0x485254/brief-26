package com.easygroup.repository;

import com.easygroup.entity.Draw;
import com.easygroup.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Group entity.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    /**
     * Find all groups for a specific draw.
     * 
     * @param draw the draw for which groups were created
     * @return a list of Group objects
     */
    List<Group> findByDraw(Draw draw);

    /**
     * Find all groups for a specific draw ordered by name.
     * 
     * @param draw the draw for which groups were created
     * @return a list of Group objects ordered by name
     */
    List<Group> findByDrawOrderByNameAsc(Draw draw);

    /**
     * Count the number of groups for a specific draw.
     * 
     * @param draw the draw for which groups were created
     * @return the number of groups for the draw
     */
    long countByDraw(Draw draw);
}
