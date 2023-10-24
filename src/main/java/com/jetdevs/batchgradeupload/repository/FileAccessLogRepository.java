package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.FileAccessLog;
import org.springframework.data.repository.CrudRepository;

public interface FileAccessLogRepository extends CrudRepository<FileAccessLog, Integer> {

}
