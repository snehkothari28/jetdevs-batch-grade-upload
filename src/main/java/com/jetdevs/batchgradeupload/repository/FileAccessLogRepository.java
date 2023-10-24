package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.FileAccessLog;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for accessing FileAccessLog entities.
 * Extends CrudRepository to provide basic CRUD operations.
 */
public interface FileAccessLogRepository extends CrudRepository<FileAccessLog, Integer> {
    // No additional methods are defined here as CrudRepository provides standard CRUD operations.
}
