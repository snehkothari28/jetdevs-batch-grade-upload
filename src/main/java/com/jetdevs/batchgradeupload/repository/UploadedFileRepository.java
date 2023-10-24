package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.UploadedFile;
import com.jetdevs.batchgradeupload.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UploadedFileRepository extends CrudRepository<UploadedFile, Integer> {

    List<UploadedFile> findByUser(User user);
}
