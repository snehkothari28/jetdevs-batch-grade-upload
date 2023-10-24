package com.jetdevs.batchgradeupload.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing a user.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object (DTO) representing a user.")
public class UserDTO {
    // Unique identifier for the user.
    @JsonIgnore
    @Schema(hidden = true)
    private Integer id;

    // User's name.
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "User's name")
    private String name;

    // Status indicating whether the user is active or inactive.
    @Schema(description = "Status indicating whether the user is active or inactive")
    private Boolean status;

    // User's password. Excluded from JSON serialization for security reasons.
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "User's password")
    @ToString.Exclude // Exclude password from printing in logs
    private String password;

    // User's role (SUPER_ADMIN, ADMIN, USER).
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "User's role (SUPER_ADMIN, ADMIN, USER)")
    private Roles role; // default role id will be user when creating role
}
