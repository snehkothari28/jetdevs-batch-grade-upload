package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository interface for accessing User entities.
 * Extends CrudRepository to provide basic CRUD operations and custom query methods.
 */
public interface UserRepository extends CrudRepository<User, Integer> {

    /**
     * Finds a User entity by its name.
     *
     * @param name The name of the user to search for.
     * @return Optional containing the User entity if found, empty otherwise.
     */
    Optional<User> findByName(String name);
}
