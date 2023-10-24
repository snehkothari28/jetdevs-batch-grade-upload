package com.jetdevs.batchgradeupload.controller;

import com.jetdevs.batchgradeupload.model.UserDTO;
import com.jetdevs.batchgradeupload.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableMethodSecurity(securedEnabled = true)
public class UserController {
    @Autowired
    private UserManagementService userManagementService;

    @PostMapping("/user/add")
    @Secured({"Super Admin", "Admin"})
    @ResponseBody
    UserDTO createUser(final @RequestBody UserDTO userDTO) {
        return userManagementService.createUser(userDTO);
    }
}
