package com.jetdevs.batchgradeupload.service;

import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.model.UserDTO;
import com.jetdevs.batchgradeupload.repository.UserRepository;
import com.jetdevs.batchgradeupload.util.Hmac512PasswordEncoder;
import com.jetdevs.batchgradeupload.util.PasswordGeneratorUtil;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
public class UserManagementService {
    Logger logger = LoggerFactory.getLogger(UserManagementService.class);
    EntityManager entityManager;
    private Hmac512PasswordEncoder hmac512PasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Value("${default.role.id}")
    private int defaultRoleId;

    public UserManagementService(EntityManager entityManager, @Value("${password.salt}") String salt) {
        this.entityManager = entityManager;
        this.hmac512PasswordEncoder = new Hmac512PasswordEncoder(salt);
    }

    @Transactional
    public Integer createUser(UserDTO userDTO) {
        logger.info("Received request to create user {}", userDTO);
        User user = new User();
        user.setName(userDTO.getName());
        String hashedPassword = getHashedPassword(userDTO.getPassword());
        user.setHashedPassword(hashedPassword);
        user.setRoleId(defaultRoleId);
        user.setStatus(true);

        try {
            userRepository.save(user);
            logger.info("Created user with id {}", user.getId());
            return user.getId();
        } catch (DataAccessException e) {
            logger.error("Failed creating user");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed creating user", e
            );
        }
    }

    @Transactional
    public void makeUserSuperAdmin(Integer userid) {
        logger.info("Received request to make role {} superadmin", userid);

        Optional<User> userOptional = userRepository.findById(userid);

        if (userOptional.isEmpty()) {
            logger.error("No user with id {} present", userid);
            throw new NullPointerException("No user with id " + userid + " present");
        }
        User user = userOptional.get();
        user.setRoleId(1);

        try {
            userRepository.save(user);
            logger.info("Assigned superadmin role to user with id {}", user.getId());
        } catch (DataAccessException e) {
            logger.error("Failed assigning superadmin role to user with id {}", user.getId());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed creating user", e
            );
        }
    }

    private String getHashedPassword(@Nullable String password) {
        if (password == null)
            password = PasswordGeneratorUtil.generateSecureRandomPassword();
        return hmac512PasswordEncoder.encode(password);
    }

}
