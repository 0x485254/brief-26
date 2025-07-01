package com.easygroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * Entity representing a shared list between users.
 * Allows a user to see a list created by another user.
 */
@Entity
@Table(name = "list_share", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "list_id", "shared_with_user_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListShare {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    @JsonIgnoreProperties({ "user", "persons", "shares", "draws" })

    private ListEntity list;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with_user_id", nullable = false)
    @JsonIgnoreProperties({ "lists", "sharedLists", "password" })

    private User sharedWithUser;
}
