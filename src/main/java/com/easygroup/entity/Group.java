package com.easygroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a group generated from a draw.
 * Each group contains multiple persons via direct many-to-many relationship.
 */
@Entity
@Table(name = "\"group\"") // Quoted because "group" is a reserved keyword in SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id", nullable = false)
    private Draw draw;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "group_person",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private List<Person> persons = new ArrayList<>();
}