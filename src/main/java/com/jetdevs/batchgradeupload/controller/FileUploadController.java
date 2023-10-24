package com.jetdevs.batchgradeupload.controller;

import com.jetdevs.batchgradeupload.entity.GradeSheet;
import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.repository.UserRepository;
import com.jetdevs.batchgradeupload.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * Controller class handling file upload and related operations.
 * This class contains endpoints for uploading files, checking file status, listing files, retrieving file contents, and deleting files.
 */
@RestController
@EnableMethodSecurity(securedEnabled = true)
@Tag(name = "File Management APIs", description = "File Management")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint for uploading a file.
     *
     * @param file The MultipartFile object representing the uploaded file.
     * @return Integer representing the ID of the uploaded file.
     * @throws IOException if an I/O error occurs while processing the file.
     */
    @Operation(summary = "Upload a file", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Integer.class)))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/file")
    @Secured({"Super Admin", "Admin", "User"})
    @ResponseBody
    Integer uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userRepository.findByName(username);

        // Delegates file upload logic to the FileUploadService.
        return fileUploadService.saveFile(file, user.orElse(null));
    }

    /**
     * Endpoint for checking the status of a specific file.
     *
     * @param fileId The ID of the file.
     * @return String representing the status of the file.
     */
    @Operation(summary = "Check file status", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = String.class)))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File status retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/file/status/{fileId}")
    @Secured({"Super Admin", "Admin", "User"})
    @ResponseBody
    String fileStatus(@PathVariable Integer fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userRepository.findByName(username);

        // Delegates file status check logic to the FileUploadService.
        return fileUploadService.fileStatus(fileId, user.orElse(null));
    }

    /**
     * Endpoint for listing all files uploaded by the user.
     *
     * @return List of Strings representing the names of uploaded files.
     */

    @Operation(summary = "List all uploaded files", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = List.class)))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Files listed successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/file/list")
    @Secured({"Super Admin", "Admin", "User"})
    @ResponseBody
    List<String> listFiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userRepository.findByName(username);

        // Delegates file listing logic to the FileUploadService.
        return fileUploadService.listFiles(user.orElse(null));
    }

    /**
     * Endpoint for retrieving the contents of a specific file.
     *
     * @param fileId The ID of the file.
     * @return List of GradeSheet objects representing the contents of the file.
     */
    @Operation(summary = "Retrieve file contents", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = List.class)))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File contents retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/file/{fileId}")
    @Secured({"Super Admin", "Admin", "User"})
    @ResponseBody
    List<GradeSheet> fileContents(@PathVariable Integer fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userRepository.findByName(username);

        // Delegates file content retrieval logic to the FileUploadService.
        return fileUploadService.fileContents(fileId, user.orElse(null));
    }

    /**
     * Endpoint for deleting a specific file.
     *
     * @param fileId The ID of the file to be deleted.
     */
    @Operation(summary = "Delete a file", responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = Void.class)))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/file/{fileId}")
    @Secured({"Super Admin", "Admin"})
    @ResponseBody
    void deleteFile(@PathVariable Integer fileId) {
        // Delegates file deletion logic to the FileUploadService.
        fileUploadService.deleteFile(fileId);
    }
}
