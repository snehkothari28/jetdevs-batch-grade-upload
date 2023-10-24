package com.jetdevs.batchgradeupload.entity;

import com.jetdevs.batchgradeupload.model.FileStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * Entity class representing an uploaded file.
 */
@Entity
@Data
public class UploadedFile {
    // Unique identifier for the uploaded file.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Timestamp indicating when the file was uploaded.
    @NotNull
    private Date uploadedTime;

    // Timestamp indicating the last access time of the file.
    @NotNull
    private Date lastAccessTime;

    // Many-to-One relationship with User entity using user_id as the foreign key.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Binary data of the file stored as a byte array.
    @Lob
    private byte[] file;

    // Name of the uploaded file.
    private String fileName;

    // Enumeration representing the status of the uploaded file (e.g., UPLOADED, PROCESSING, PROCESSED).
    @Enumerated(EnumType.STRING)
    private FileStatus status;
}
