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
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Service class handling user management operations such as user creation, role assignment, and password hashing.
 */
@Service
public class UserManagementService {

    // Password encoder using HMAC-SHA-512 algorithm
    private final Hmac512PasswordEncoder hmac512PasswordEncoder;
    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(UserManagementService.class);
    // Entity manager for database operations
    EntityManager entityManager;
    // Roles available in the system
    private Role userRole;
    private Role adminRole;
    private Role superAdminRole;

    /**
     * Constructor for UserManagementService.
     *
     * @param entityManager  Entity manager for database operations.
     * @param salt           Salt value for password hashing.
     * @param roleRepository Repository for accessing role data.
     * @param userRepository Repository for accessing user data.
     */
    public UserManagementService(EntityManager entityManager, @Value("${password.salt}") String salt, RoleRepository roleRepository, UserRepository userRepository, Environment environment) {
        this.entityManager = entityManager;
        this.hmac512PasswordEncoder = new Hmac512PasswordEncoder(salt);
        this.userRepository = userRepository;


        if (!environment.matchesProfiles("test")) {
            // Initialize roles from the repository
            userRole = roleRepository.findById(3).orElse(new Role());
            superAdminRole = roleRepository.findById(1).orElse(new Role());
            adminRole = roleRepository.findById(2).orElse(new Role());
        }
    }

    /**
     * Maps UserDTO to User entity, sets the password, and creates a UserDTO object.
     *
     * @param userDTO  UserDTO object containing user data.
     * @param password Password for initial communication.
     * @param user     User entity.
     * @return UserDTO object containing user data.
     */
    private static UserDTO mapToUserDto(UserDTO userDTO, String password, User user) {
        UserDTO response = new UserDTO();
        response.setPassword(password);
        response.setName(userDTO.getName());
        response.setId(user.getId());
        return response;
    }

    /**
     * Creates a new user in the system.
     *
     * @param userDTO UserDTO object containing user data.
     * @return UserDTO object containing user data after creation.
     * @throws ResponseStatusException If user with the same name already exists or if user creation fails.
     */
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        logger.info("Received request to create user {}", userDTO);
        // Check if user with the same name already exists
        if (checkIfSameUserExists(userDTO.getName())) {
            logger.error("User of name {} already exists", userDTO.getName());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User with the same name already exists."
            );
        }
        // Create a new user entity
        User user = new User();
        user.setName(userDTO.getName());
        // Generate a password if not provided
        String password = userDTO.getPassword();
        if (password == null)
            password = PasswordGeneratorUtil.generateSecureRandomPassword();
        // Hash the password using HMAC-SHA-512 algorithm
        String hashedPassword = getHashedPassword(password);
        user.setHashedPassword(hashedPassword);
        user.setRole(userRole);
        user.setStatus(true);

        try {
            // Save the user entity to the database
            userRepository.save(user);
            logger.info("Created user with id {}", user.getId());
            // Return a UserDTO with password (for initial communication) and other user details
            return mapToUserDto(userDTO, password, user);
        } catch (DataAccessException e) {
            // Handle database exception
            logger.error("Failed creating user");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed creating user.", e
            );
        }
    }

    /**
     * Assigns the superadmin role to the specified user.
     *
     * @param userId ID of the user to be assigned the superadmin role.
     * @throws ResponseStatusException If the user is not found or if assigning the role fails.
     */
    @Transactional
    public void makeUserSuperAdmin(Integer userId) {
        logger.info("Received request to make user {} - superadmin", userId);
        // Find the user by ID
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            // If user not found, throw an exception
            logger.error("No user with id {} present", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with the specified ID found.");
        }
        // Get the user from Optional
        User user = userOptional.get();
        // Set the role to superadmin
        user.setRole(superAdminRole);

        try {
            // Save the updated user entity to the database
            userRepository.save(user);
            logger.info("Assigned superadmin role to user with id {}", user.getId());
        } catch (DataAccessException e) {
            // Handle database exception
            logger.error("Failed assigning superadmin role to user with id {}", user.getId());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed assigning role.", e
            );
        }
    }

    /**
     * Hashes the password using HMAC-SHA-512 algorithm.
     *
     * @param password Plain text password to be hashed.
     * @return Hashed password.
     */
    private String getHashedPassword(String password) {
        return hmac512PasswordEncoder.encode(password);
    }

    /**
     * Checks if a user with the given username already exists in the database.
     *
     * @param username Username to be checked.
     * @return True if user with the given username exists, false otherwise.
     */
    private boolean checkIfSameUserExists(String username) {
        return userRepository.findByName(username).isPresent();
    }

    /**
     * Changes the role of the specified user.
     *
     * @param userDTO UserDTO object containing user data and the new role.
     * @return UserDTO object containing updated user data.
     * @throws ResponseStatusException If the user is not found, if the specified role is incorrect, or if role assignment fails.
     */
    public UserDTO changeRole(UserDTO userDTO) {
        logger.info("Received request to change role for user {} to {}", userDTO.getName(), userDTO.getRole());
        // Find the user by username
        User user = userRepository.findByName(userDTO.getName()).orElse(null);
        Roles role = userDTO.getRole();
        // Check if the user and role are valid
        if (role == Roles.SUPER_ADMIN) {
            // Superadmin role cannot be assigned via this method, throw an exception
            logger.error("Setting role {} not allowed", userDTO.getRole());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Incorrect role."
            );
        }
        if (user == null) {
            // If user not found, throw an exception
            logger.error("User of name {} doesn't exist", userDTO.getName());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User with the specified name not found."
            );
        }

        // Assign the appropriate role based on the UserDTO input
        user.setRole(role == Roles.ADMIN ? adminRole : userRole);
        try {
            // Save the updated user entity to the database
            userRepository.save(user);
            logger.info("Assigned role to user with id {}", user.getId());
            // Return a UserDTO with updated user data
            return mapToUserDto(userDTO, null, user);
        } catch (DataAccessException e) {
            // Handle database exception
            logger.error("Failed assigning role to user with id {}", user.getId());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed assigning role.", e
            );
        }
    }
}
