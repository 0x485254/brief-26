package com.easygroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a list of people created by a user.
 * Lists can be shared with other users.
 */
@Entity
@Table(name = "list", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_shared", nullable = false)
    private Boolean isShared = false;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Person> persons = new ArrayList<>();

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListShare> shares = new ArrayList<>();

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Draw> draws = new ArrayList<>();
}