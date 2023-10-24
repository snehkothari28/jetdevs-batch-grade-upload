package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.GradeSheet;
import com.jetdevs.batchgradeupload.entity.UploadedFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository interface for accessing GradeSheet entities.
 * Extends CrudRepository to provide basic CRUD operations and custom query methods.
 */
public interface GradeSheetRepository extends CrudRepository<GradeSheet, Integer> {

    /**
     * Finds a list of GradeSheet entities associated with a specific UploadedFile.
     *
     * @param file The UploadedFile object to which GradeSheet entities are associated.
     * @return List of GradeSheet entities associated with the provided UploadedFile.
     */
    List<GradeSheet> findByFile(UploadedFile file);

    /**
     * Deletes GradeSheet entities associated with a specific UploadedFile.
     *
     * @param file The UploadedFile object to which GradeSheet entities are associated.
     */
    void deleteByFile(UploadedFile file);
}
