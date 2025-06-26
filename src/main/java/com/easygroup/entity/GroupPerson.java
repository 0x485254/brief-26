package com.easygroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Entity representing the association between a group and a person.
 * This is a join table for the many-to-many relationship between Group and Person.
 */
@Entity
@Table(name = "group_person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(GroupPerson.GroupPersonId.class)
public class GroupPerson {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    /**
     * Composite key class for GroupPerson entity.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupPersonId implements Serializable {
        private Integer group;
        private Integer person;
    }
}
