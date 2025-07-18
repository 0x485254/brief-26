package com.easygroup.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Entity representing a user in the system.
 * Users can create and share lists of people.
 */
@Entity
@Table(name = "\"user\"") // Quoted because "user" is a reserved keyword in PostgreSQL
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"lists", "sharedLists"})

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(name = "cgu_date")
    private LocalDate cguDate;

    @Column(name = "is_activated", nullable = false)
    private Boolean isActivated = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private java.util.List<ListEntity> lists = new ArrayList<>();

    @OneToMany(mappedBy = "sharedWithUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private java.util.List<ListShare> sharedLists = new ArrayList<>();

    /**
     * Enum representing user roles in the system.
     */
    public enum Role {
        USER, ADMIN
    }

    /**
     * Sets creation and update timestamps before persisting.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the update timestamp before updating.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
