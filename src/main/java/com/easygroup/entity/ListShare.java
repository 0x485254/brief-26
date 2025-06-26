package com.easygroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a shared list between users.
 * Allows a user to see a list created by another user.
 */
@Entity
@Table(name = "list_share", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"list_id", "shared_with_user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private ListEntity list;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with_user_id", nullable = false)
    private User sharedWithUser;
}