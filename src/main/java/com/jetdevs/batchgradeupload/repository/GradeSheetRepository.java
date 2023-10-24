package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.GradeSheet;
import com.jetdevs.batchgradeupload.entity.UploadedFile;
import com.jetdevs.batchgradeupload.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GradeSheetRepository extends CrudRepository<GradeSheet, Integer> {

    List<GradeSheet> findByFile(UploadedFile file);

    void deleteByFile(UploadedFile file);
}
