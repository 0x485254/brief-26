package com.easygroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a person in a list.
 * Each person has various attributes used for group creation.
 */
@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "french_level", nullable = false)
    private Integer frenchLevel;

    @Column(name = "old_dwwm", nullable = false)
    private Boolean oldDwwm;

    @Column(name = "tech_level", nullable = false)
    private Integer techLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private ListEntity list;

    @OneToMany(mappedBy = "person")
    private List<GroupPerson> groupPersons = new ArrayList<>();

    /**
     * Enum representing gender options.
     */
    public enum Gender {
        FEMALE, MALE, OTHER
    }

    /**
     * Enum representing personality profile options.
     */
    public enum Profile {
        A_LAISE, RESERVE, TIMIDE
    }
}