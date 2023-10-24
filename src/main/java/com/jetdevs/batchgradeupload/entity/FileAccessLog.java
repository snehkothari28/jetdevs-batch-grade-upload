package com.jetdevs.batchgradeupload.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

/**
 * Entity class representing the access log of a file.
 */
@Entity
@Data
public class FileAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Many-to-One relationship with UploadedFile entity using file_id as the foreign key.
    @ManyToOne
    @JoinColumn(name = "file_id")
    private UploadedFile file;

    // Timestamp indicating the time of access to the file.
    private Date accessTime;

    // Many-to-One relationship with User entity using user_id as the foreign key.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
