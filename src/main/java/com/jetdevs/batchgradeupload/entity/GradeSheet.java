package com.jetdevs.batchgradeupload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 * Entity class representing a grade sheet.
 */
@Entity
@Data
public class GradeSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Enrollment number of the student associated with this grade sheet.
    private int enrollmentNumber;

    // Grade obtained by the student.
    private int grade;

    // Subject for which the grade is recorded.
    private String subject;

    // Many-to-One relationship with UploadedFile entity using file_id as the foreign key.
    @ManyToOne
    @JoinColumn(name = "file_id")
    @JsonIgnore // Prevents JSON serialization of the associated UploadedFile object to avoid circular dependencies.
    private UploadedFile file;
}
