package com.jetdevs.batchgradeupload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entity representing a grade sheet.")
public class GradeSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Integer id;

    // Enrollment number of the student associated with this grade sheet.
    @Schema(description = "Enrollment number of the student", requiredMode = Schema.RequiredMode.REQUIRED)
    private int enrollmentNumber;

    // Grade obtained by the student.
    @Schema(description = "Grade obtained by the student", requiredMode = Schema.RequiredMode.REQUIRED)
    private int grade;

    // Subject for which the grade is recorded.
    @Schema(description = "Subject for which the grade is recorded", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subject;

    // Many-to-One relationship with UploadedFile entity using file_id as the foreign key.
    @ManyToOne
    @JoinColumn(name = "file_id")
    @JsonIgnore // Prevents JSON serialization of the associated UploadedFile object to avoid circular dependencies.
    private UploadedFile file;
}
