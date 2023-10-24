package com.jetdevs.batchgradeupload.controller;

import com.jetdevs.batchgradeupload.entity.GradeSheet;
import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.repository.UserRepository;
import com.jetdevs.batchgradeupload.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@EnableMethodSecurity(securedEnabled = true)
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/file")
    @Secured({"Super Admin", "Admin", "User"})
    @ResponseBody
    Integer uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userRepository.findByName(username);

        return fileUploadService.saveFile(file, user.orElse(null));
    }

    @GetMapping("/file/status/{fileId}")
    @Secured({"Super Admin", "Admin", "User"})
    @ResponseBody
    String fileStatus(@PathVariable Integer fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userRepository.findByName(username);

        return fileUploadService.fileStatus(fileId, user.orElse(null));
    }

    @GetMapping("/file/list")
    @Secured({"Super Admin", "Admin", "User"})
    @ResponseBody
    List<String> listFiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userRepository.findByName(username);

        return fileUploadService.listFiles(user.orElse(null));
    }

    @GetMapping("/file/{fileId}")
    @Secured({"Super Admin", "Admin", "User"})
    @ResponseBody
    List<GradeSheet> fileContents(@PathVariable Integer fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userRepository.findByName(username);

        return fileUploadService.fileContents(fileId, user.orElse(null));
    }

    @DeleteMapping("/file/{fileId}")
    @Secured({"Super Admin", "Admin"})
    @ResponseBody
    void deleteFile(@PathVariable Integer fileId) {
        fileUploadService.deleteFile(fileId);
    }
}
