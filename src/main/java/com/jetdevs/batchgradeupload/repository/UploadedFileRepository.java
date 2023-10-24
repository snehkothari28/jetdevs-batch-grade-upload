package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.UploadedFile;
import org.springframework.data.repository.CrudRepository;

public interface UploadedFileRepository extends CrudRepository<UploadedFile, Integer> {

}
