package com.jetdevs.batchgradeupload.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class FileAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private int fileId;

    private Date accessTime;

    private int userId;

}
