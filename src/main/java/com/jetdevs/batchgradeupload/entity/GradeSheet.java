package com.jetdevs.batchgradeupload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class GradeSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int enrollmentNumber;

    private int grade;

    private String subject;

    @ManyToOne
    @JoinColumn(name = "file_id")
    @JsonIgnore
    private UploadedFile file;
}
