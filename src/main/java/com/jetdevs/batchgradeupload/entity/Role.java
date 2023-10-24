package com.jetdevs.batchgradeupload.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Entity class representing a user role.
 */
@Entity
@Data
public class Role {
    // Unique identifier for the role.
    @Id
    private Integer id;

    // Name of the role, e.g., "Admin", "User".
    private String name;

    // Status indicating whether the role is active or inactive.
    private boolean status;
}
