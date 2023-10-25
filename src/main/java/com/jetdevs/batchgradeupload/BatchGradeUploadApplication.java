package com.jetdevs.batchgradeupload;

import com.jetdevs.batchgradeupload.entity.Role;
import com.jetdevs.batchgradeupload.model.Roles;
import com.jetdevs.batchgradeupload.model.UserDTO;
import com.jetdevs.batchgradeupload.repository.RoleRepository;
import com.jetdevs.batchgradeupload.service.UserManagementService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchGradeUploadApplication implements CommandLineRunner {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(BatchGradeUploadApplication.class, args);
    }

    // CommandLineRunner interface method to run the application logic
    @Override
    public void run(String... args) throws Exception {
        // Uncomment the next line to initialize the database with roles and a super admin user
//        initializeDatabase();
    }

    // Method to initialize the database with roles and a super admin user
    @Transactional
    private void initializeDatabase() {
        createRoles();       // Create roles: Super Admin, Admin, User
        createSuperAdmin();  // Create a super admin user and assign the super admin role
    }

    // Method to create a super admin user and assign the super admin role
    private void createSuperAdmin() {
        // Temporary hardcoded super admin user credentials
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Sneh");
        userDTO.setPassword("sneh1234");
        // Create the user and obtain the user's ID
        Integer userId = userManagementService.createUser(userDTO).getId();

        // Assign the super admin role to the user
        userManagementService.makeUserSuperAdmin(userId);
    }

    // Method to create roles (Super Admin, Admin, User) in the database
    @Transactional
    private void createRoles() {
        // Create Super Admin role
        Role role = new Role();
        role.setId(1);
        role.setName("Super Admin");
        role.setStatus(true);
        roleRepository.save(role);

        // Create Admin role
        role = new Role();
        role.setId(2);
        role.setName("Admin");
        role.setStatus(true);
        roleRepository.save(role);

        // Create User role
        role = new Role();
        role.setId(3);
        role.setName("User");
        role.setStatus(true);
        roleRepository.save(role);
    }
}
