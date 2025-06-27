package com.easygroup.service;

import com.easygroup.entity.Draw;
import com.easygroup.entity.Group;
import com.easygroup.entity.ListEntity;
import com.easygroup.entity.Person;
import com.easygroup.repository.DrawRepository;
import com.easygroup.repository.GroupRepository;
import com.easygroup.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing draws and creating groups.
 */
@Service
@Transactional
public class DrawService {

    private final DrawRepository drawRepository;
    private final GroupRepository groupRepository;
    private final PersonRepository personRepository;

    public DrawService(DrawRepository drawRepository, GroupRepository groupRepository, PersonRepository personRepository) {
        this.drawRepository = drawRepository;
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
    }

    /**
     * Find all draws.
     *
     * @return a list of all draws
     */
    public List<Draw> findAll() {
        return drawRepository.findAll();
    }

    /**
     * Find a draw by ID.
     *
     * @param id the draw ID
     * @return an Optional containing the draw if found
     */
    public Optional<Draw> findById(UUID id) {
        return drawRepository.findById(id);
    }

    /**
     * Find all draws for a list.
     *
     * @param list the list for which draws were created
     * @return a list of draws for the list
     */
    public List<Draw> findByList(ListEntity list) {
        return drawRepository.findByList(list);
    }

    /**
     * Find all draws for a list ordered by creation date (newest first).
     *
     * @param list the list for which draws were created
     * @return a list of draws for the list ordered by creation date
     */
    public List<Draw> findByListOrderByCreatedAtDesc(ListEntity list) {
        return drawRepository.findByListOrderByCreatedAtDesc(list);
    }

    /**
     * Save a draw.
     *
     * @param draw the draw to save
     * @return the saved draw
     */
    public Draw save(Draw draw) {
        return drawRepository.save(draw);
    }

    /**
     * Delete a draw by ID.
     *
     * @param id the draw ID
     */
    public void deleteById(UUID id) {
        drawRepository.deleteById(id);
    }

    /**
     * Create a new draw with random groups.
     *
     * @param list the list of persons to create groups from
     * @param numberOfGroups the number of groups to create
     * @param title the title of the draw
     * @return the created draw with groups
     */
    public Draw createDraw(ListEntity list, int numberOfGroups, String title) {
        // Create a new draw
        Draw draw = new Draw();
        draw.setList(list);
        draw.setTitle(title);
        draw = drawRepository.save(draw);

        // Get all persons from the list
        List<Person> persons = personRepository.findByList(list);

        // Shuffle the persons to randomize the groups
        Collections.shuffle(persons);

        // Create the groups
        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < numberOfGroups; i++) {
            Group group = new Group();
            group.setName("Group " + (i + 1));
            group.setDraw(draw);
            groups.add(groupRepository.save(group));
        }

        // This is a placeholder for the actual group creation logic
        // In a real implementation, you would need to distribute persons to groups
        // based on various criteria and create GroupPerson associations

        return draw;
    }

    /**
     * Count the number of draws for a list.
     *
     * @param list the list for which draws were created
     * @return the number of draws for the list
     */
    public long countByList(ListEntity list) {
        return drawRepository.countByList(list);
    }
}
