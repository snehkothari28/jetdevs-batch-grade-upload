package com.jetdevs.batchgradeupload.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing a user.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    // Unique identifier for the user.
    @JsonIgnore
    private Integer id;

    // User's name.
    private String name;

    // Status indicating whether the user is active or inactive.
    private Boolean status;

    // User's password. Excluded from JSON serialization for security reasons.
    @ToString.Exclude // Exclude password from printing in logs
    private String password;

    // User's role (SUPER_ADMIN, ADMIN, USER).
    private Roles role; // default role id will be user when creating role
}
