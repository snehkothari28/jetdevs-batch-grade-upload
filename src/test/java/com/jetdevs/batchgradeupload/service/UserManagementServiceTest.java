package com.jetdevs.batchgradeupload.service;

import com.jetdevs.batchgradeupload.entity.Role;
import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.model.Roles;
import com.jetdevs.batchgradeupload.model.UserDTO;
import com.jetdevs.batchgradeupload.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserManagementServiceTest {

    @Mock
    private Logger logger;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManagementService userManagementService;

    private Role userRole;
    private Role adminRole;
    private Role superAdminRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(3);
        userRole.setName("USER");

        adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName("ADMIN");

        superAdminRole = new Role();
        superAdminRole.setId(1);
        superAdminRole.setName("SUPER_ADMIN");

//        userManagementService = new UserManagementService(null, "salt", null, userRepository);
    }

    @Test
    void testCreateUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("testuser");

        when(userRepository.findByName(userDTO.getName())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        UserDTO createdUser = userManagementService.createUser(userDTO);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getPassword());
        assertEquals(userDTO.getName(), createdUser.getName());
    }

    @Test
    void testCreateUserWithExistingUsername() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("existinguser");

        when(userRepository.findByName(userDTO.getName())).thenReturn(Optional.of(new User()));

        assertThrows(ResponseStatusException.class, () -> userManagementService.createUser(userDTO));
    }

    @Test
    void testMakeUserSuperAdmin() {
        User user = new User();
        user.setId(1);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userManagementService.makeUserSuperAdmin(user.getId()));
        assertEquals(superAdminRole, user.getRole());
    }

    @Test
    void testMakeUserSuperAdminWithNonexistentUser() {
        int userId = 999;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userManagementService.makeUserSuperAdmin(userId));
    }

    @Test
    void testChangeRoleToAdmin() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("testuser");
        userDTO.setRole(Roles.ADMIN);

        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setRole(userRole);

        when(userRepository.findByName(userDTO.getName())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDTO updatedUser = userManagementService.changeRole(userDTO);

        assertNotNull(updatedUser);
        assertEquals(existingUser.getId(), updatedUser.getId());
        assertEquals(Roles.ADMIN, userDTO.getRole());
    }

    @Test
    void testChangeRoleToInvalidRole() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("testuser");
        userDTO.setRole(Roles.SUPER_ADMIN); // Invalid role

        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setRole(userRole);

        when(userRepository.findByName(userDTO.getName())).thenReturn(Optional.of(existingUser));

        assertThrows(ResponseStatusException.class, () -> userManagementService.changeRole(userDTO));
    }

    @Test
    void testChangeRoleWithNonexistentUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("nonexistentuser");
        userDTO.setRole(Roles.ADMIN);

        when(userRepository.findByName(userDTO.getName())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userManagementService.changeRole(userDTO));
    }
}
