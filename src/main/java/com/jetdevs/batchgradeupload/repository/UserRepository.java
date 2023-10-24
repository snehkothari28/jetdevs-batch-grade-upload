package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}
