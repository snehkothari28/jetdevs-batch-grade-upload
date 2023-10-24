package com.jetdevs.batchgradeupload.controller;

import com.jetdevs.batchgradeupload.model.UserDTO;
import com.jetdevs.batchgradeupload.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class handling user-related HTTP requests.
 * This class contains endpoints for creating and modifying users.
 */
@RestController
@EnableMethodSecurity(securedEnabled = true)
public class UserController {

    @Autowired
    private UserManagementService userManagementService;

    /**
     * Endpoint for creating a new user.
     *
     * @param userDTO The UserDTO object containing user information.
     * @return UserDTO representing the created user.
     */
    @PostMapping("/user")
    @Secured({"Super Admin", "Admin"})
    @ResponseBody
    UserDTO createUser(final @RequestBody UserDTO userDTO) {
        // Delegates user creation logic to the UserManagementService.
        return userManagementService.createUser(userDTO);
    }

    /**
     * Endpoint for changing user roles.
     * Only accessible to users with 'Super Admin' privileges.
     *
     * @param userDTO The UserDTO object containing user information and the new role.
     * @return UserDTO representing the user with modified role.
     */
    @PutMapping("/user")
    @Secured({"Super Admin"})
    @ResponseBody
    UserDTO changeRole(final @RequestBody UserDTO userDTO) {
        // Delegates role change logic to the UserManagementService.
        return userManagementService.changeRole(userDTO); // make admin
    }
}
