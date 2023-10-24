package com.jetdevs.batchgradeupload;

import com.jetdevs.batchgradeupload.entity.Role;
import com.jetdevs.batchgradeupload.model.UserDTO;
import com.jetdevs.batchgradeupload.repository.RoleRepository;
import com.jetdevs.batchgradeupload.service.UserManagementService;
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

    @Override
    public void run(String... args) throws Exception {

        createRoles();
        createSuperAdmin();

    }

    private void createSuperAdmin() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Sneh");
        userDTO.setPassword("sneh1234");
        Integer userId = userManagementService.createUser(userDTO);
        userManagementService.makeUserSuperAdmin(userId);
    }

    private void createRoles() {
        Role role = new Role();
        role.setId(1);
        role.setName("Super Admin");
        role.setStatus(true);
        roleRepository.save(role);

        role = new Role();
        role.setId(2);
        role.setName("Admin");
        role.setStatus(true);
        roleRepository.save(role);

        role = new Role();
        role.setId(3);
        role.setName("User");
        role.setStatus(true);
        roleRepository.save(role);
    }
}
