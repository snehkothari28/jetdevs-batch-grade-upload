package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.UploadedFile;
import com.jetdevs.batchgradeupload.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository interface for accessing UploadedFile entities.
 * Extends CrudRepository to provide basic CRUD operations and custom query methods.
 */
public interface UploadedFileRepository extends CrudRepository<UploadedFile, Integer> {

    /**
     * Finds a list of UploadedFile entities associated with a specific User.
     *
     * @param user The User object to which UploadedFile entities are associated.
     * @return List of UploadedFile entities associated with the provided User.
     */
    List<UploadedFile> findByUser(User user);
}
