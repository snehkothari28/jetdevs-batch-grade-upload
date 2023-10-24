package com.jetdevs.batchgradeupload.entity;

import com.jetdevs.batchgradeupload.model.FileStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Date uploadedTime;

    private int userId;

    @Lob
    private byte[] file;

    private FileStatus status;
}
