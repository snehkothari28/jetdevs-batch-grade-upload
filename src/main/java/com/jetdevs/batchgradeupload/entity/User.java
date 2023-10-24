package com.jetdevs.batchgradeupload.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Entity class representing a user in the system.
 */
@Entity
@Data
public class User {
    // Unique identifier for the user.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // User's name.
    @NotNull
    private String name;

    // Hashed password of the user.
    @NotNull
    private String hashedPassword;

    // Many-to-One relationship with Role entity using role_id as the foreign key.
    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // Status indicating whether the user is active or inactive.
    private boolean status;
}
