package com.jetdevs.batchgradeupload.service;

import com.jetdevs.batchgradeupload.entity.Role;
import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.model.Roles;
import com.jetdevs.batchgradeupload.model.UserDTO;
import com.jetdevs.batchgradeupload.repository.RoleRepository;
import com.jetdevs.batchgradeupload.repository.UserRepository;
import com.jetdevs.batchgradeupload.util.Hmac512PasswordEncoder;
import com.jetdevs.batchgradeupload.util.PasswordGeneratorUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
public class UserManagementService {
    private final Role userRole;
    private final Role adminRole;
    private final Role superAdminRole;
    private final Hmac512PasswordEncoder hmac512PasswordEncoder;
    Logger logger = LoggerFactory.getLogger(UserManagementService.class);
    EntityManager entityManager;
    private UserRepository userRepository;


    public UserManagementService(EntityManager entityManager, @Value("${password.salt}") String salt, RoleRepository roleRepository, UserRepository userRepository) {
        this.entityManager = entityManager;
        this.hmac512PasswordEncoder = new Hmac512PasswordEncoder(salt);
        this.userRepository = userRepository;
        userRole = roleRepository.findById(3).orElseThrow(NullPointerException::new);
        superAdminRole = roleRepository.findById(1).orElseThrow(NullPointerException::new);
        adminRole = roleRepository.findById(2).orElseThrow(NullPointerException::new);
    }

    private static UserDTO mapToUserDto(UserDTO userDTO, String password, User user) {
        UserDTO response = new UserDTO();
        response.setPassword(password);
        response.setName(userDTO.getName());
        response.setId(user.getId());
        return response;
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        logger.info("Received request to create user {}", userDTO);
        if (checkIfSameUserExists(userDTO.getName())) {
            logger.error("User of name {} already exists", userDTO.getName());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User of already exists"
            );
        }
        User user = new User();
        user.setName(userDTO.getName());
        String password = userDTO.getPassword();
        if (password == null)
            password = PasswordGeneratorUtil.generateSecureRandomPassword();
        String hashedPassword = getHashedPassword(password);
        user.setHashedPassword(hashedPassword);
        user.setRole(userRole);
        user.setStatus(true);

        try {
            userRepository.save(user);
            logger.info("Created user with id {}", user.getId());
            return mapToUserDto(userDTO, password, user);
        } catch (DataAccessException e) {
            logger.error("Failed creating user");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed creating user", e
            );
        }
    }

    @Transactional
    public void makeUserSuperAdmin(Integer userid) {
        logger.info("Received request to make user {} - superadmin", userid);

        Optional<User> userOptional = userRepository.findById(userid);

        if (userOptional.isEmpty()) {
            logger.error("No user with id {} present", userid);
            throw new NullPointerException("No user with id " + userid + " present");
        }
        User user = userOptional.get();
        user.setRole(superAdminRole);

        try {
            userRepository.save(user);
            logger.info("Assigned superadmin role to user with id {}", user.getId());
        } catch (DataAccessException e) {
            logger.error("Failed assigning superadmin role to user with id {}", user.getId());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed assigning role", e
            );
        }
    }

    private String getHashedPassword(String password) {
        return hmac512PasswordEncoder.encode(password);
    }

    private boolean checkIfSameUserExists(String username) {
        return userRepository.findByName(username).isPresent();
    }

    public UserDTO changeRole(UserDTO userDTO) {
        logger.info("Received request to make user {} - {}", userDTO.getName(), userDTO.getRole());
        User user = userRepository.findByName(userDTO.getName()).orElse(null);
        Roles role = userDTO.getRole();
        if (role == Roles.SUPER_ADMIN) {
            logger.error("Setting role {} not allowed", userDTO.getRole());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Incorrect role"
            );
        }
        if (user == null) {
            logger.error("User of name {} doesn't exists", userDTO.getName());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User doesn't exists"
            );
        }

        user.setRole(role == Roles.ADMIN ? adminRole : userRole);
        try {
            userRepository.save(user);
            logger.info("Assigned role to user with id {}", user.getId());
            return mapToUserDto(userDTO, null, user);
        } catch (DataAccessException e) {
            logger.error("Failed assigning role to user with id {}", user.getId());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed assigning role", e
            );
        }
    }
}
