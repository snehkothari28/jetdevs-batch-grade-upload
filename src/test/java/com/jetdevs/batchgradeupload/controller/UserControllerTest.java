package com.jetdevs.batchgradeupload.controller;

import com.jetdevs.batchgradeupload.model.UserDTO;
import com.jetdevs.batchgradeupload.service.UserManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserManagementService userManagementService;

    @Test
    void testCreateUser() {
        UserDTO userDTO = new UserDTO();
        when(userManagementService.createUser(userDTO)).thenReturn(userDTO);

        UserDTO createdUser = userController.createUser(userDTO);

        assertEquals(userDTO, createdUser);
    }

    @Test
    void testChangeRole() {
        UserDTO userDTO = new UserDTO();
        when(userManagementService.changeRole(userDTO)).thenReturn(userDTO);

        UserDTO modifiedUser = userController.changeRole(userDTO);

        assertEquals(userDTO, modifiedUser);
    }
}
