package com.jetdevs.batchgradeupload.controller;

import com.jetdevs.batchgradeupload.model.UserDTO;
import com.jetdevs.batchgradeupload.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserManagementService userManagementService;

    @PostMapping("/user/add")
    @ResponseBody
    void createUser(@RequestBody UserDTO role) {
        userManagementService.createUser(role);
    }
}
