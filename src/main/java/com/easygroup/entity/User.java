package com.easygroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a user in the system.
 * Users can create and share lists of people.
 */
@Entity
@Table(name = "\"user\"") // Quoted because "user" is a reserved keyword in PostgreSQL
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private List<ListEntity> lists = new ArrayList<>();

    @OneToMany(mappedBy = "sharedWithUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListShare> sharedLists = new ArrayList<>();

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
