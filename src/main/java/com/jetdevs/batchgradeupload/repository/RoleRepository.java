package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.Role;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for accessing Role entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface RoleRepository extends CrudRepository<Role, Integer> {
    // No additional methods are defined here as CrudRepository provides standard CRUD operations.
}
