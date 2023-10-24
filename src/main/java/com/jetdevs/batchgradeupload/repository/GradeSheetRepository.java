package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.GradeSheet;
import com.jetdevs.batchgradeupload.entity.UploadedFile;
import org.springframework.data.repository.CrudRepository;

public interface GradeSheetRepository extends CrudRepository<GradeSheet, Integer> {

}
