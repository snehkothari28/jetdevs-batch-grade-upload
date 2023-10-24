package com.jetdevs.batchgradeupload.repository;

import com.jetdevs.batchgradeupload.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByName(String name);
}
