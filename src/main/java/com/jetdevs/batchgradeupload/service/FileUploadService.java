package com.jetdevs.batchgradeupload.service;

import com.jetdevs.batchgradeupload.entity.FileAccessLog;
import com.jetdevs.batchgradeupload.entity.GradeSheet;
import com.jetdevs.batchgradeupload.entity.UploadedFile;
import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.model.FileStatus;
import com.jetdevs.batchgradeupload.repository.FileAccessLogRepository;
import com.jetdevs.batchgradeupload.repository.GradeSheetRepository;
import com.jetdevs.batchgradeupload.repository.UploadedFileRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Service class handling file upload and processing operations.
 */
@Service
public class FileUploadService {

    // Executor service to handle asynchronous file processing
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    private GradeSheetRepository gradeSheetRepository;

    @Autowired
    private FileAccessLogRepository fileAccessLogRepository;

    /**
     * Saves the uploaded file and initiates asynchronous processing.
     *
     * @param file The uploaded file.
     * @param user The user uploading the file.
     * @return The ID of the saved file entity.
     * @throws ResponseStatusException If user or file is not found, or incorrect file format.
     * @throws IOException             If an error occurs while reading the file.
     */
    @Transactional
    public Integer saveFile(MultipartFile file, @Nullable User user) throws ResponseStatusException, IOException {
        // Validate user and file
        Objects.requireNonNull(user, "User not found");
        if (!file.getOriginalFilename().endsWith(".xls") && !file.getOriginalFilename().endsWith(".xlsx")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excel file required");
        }

        // Create UploadedFile entity and save it to the database
        UploadedFile fileEntity = new UploadedFile();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFile(file.getBytes());
        fileEntity.setUser(user);
        fileEntity.setUploadedTime(new Date());
        fileEntity.setLastAccessTime(new Date());
        fileEntity.setStatus(FileStatus.UPLOADED);
        uploadedFileRepository.save(fileEntity);

        // Initiate asynchronous processing of the file
        processExcelFileAsync(fileEntity, file);

        logger.info("File save successful");
        return fileEntity.getId();
    }

    /**
     * Asynchronously processes the Excel file and saves data to the database.
     *
     * @param uploadedFile  The UploadedFile entity associated with the Excel file.
     * @param multipartFile The Excel file.
     */
    private void processExcelFileAsync(UploadedFile uploadedFile, MultipartFile multipartFile) {
        executorService.submit(() -> {
            try {
                logger.info("Converting file");
                // Read and process the Excel file using Apache POI
                Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());
                processAndSaveDataFromExcel(workbook, uploadedFile);
                workbook.close();
                uploadedFile.setStatus(FileStatus.DUMPED);
                logger.info("Converting Successful");
            } catch (Exception e) {
                uploadedFile.setStatus(FileStatus.ERROR);
                // Handle exceptions appropriately (log or throw further)
                logger.error("Error converting file", e);
            }
            uploadedFileRepository.save(uploadedFile);
        });
    }

    /**
     * Processes and saves data from the Excel workbook to the database.
     *
     * @param workbook     The Excel workbook to process.
     * @param uploadedFile The UploadedFile entity associated with the workbook.
     */
    private void processAndSaveDataFromExcel(Workbook workbook, UploadedFile uploadedFile) {
        Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
        List<GradeSheet> gradeSheetList = new ArrayList<>();

        // Iterate through rows starting from the second row (index 1)
        for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                int enrollmentNumber = (int) row.getCell(0).getNumericCellValue();
                int grade = (int) row.getCell(1).getNumericCellValue();
                String subject = row.getCell(2).getStringCellValue();

                // Create a new GradeSheet entity and save it to the list
                GradeSheet gradeSheet = new GradeSheet();
                gradeSheet.setFile(uploadedFile);
                gradeSheet.setEnrollmentNumber(enrollmentNumber);
                gradeSheet.setGrade(grade);
                gradeSheet.setSubject(subject);
                gradeSheetList.add(gradeSheet);
            }
        }

        // Save all GradeSheet entities to the database
        gradeSheetRepository.saveAll(gradeSheetList);
    }

    /**
     * Retrieves the status of the specified uploaded file for the given user.
     *
     * @param fileId The ID of the uploaded file.
     * @param user   The user requesting the file status.
     * @return The status of the uploaded file (FileStatus).
     * @throws ResponseStatusException If the user doesn't have access to the file or the file is not found.
     */
    @Transactional
    public String fileStatus(Integer fileId, @Nullable User user) {
        Objects.requireNonNull(user, "User not found");
        UploadedFile uploadedFile = uploadedFileRepository.findById(fileId).orElse(null);
        if (uploadedFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not found");
        }

        // Check if user has access rights to the file
        if (user.getRole().getId() == 3 && Objects.equals(uploadedFile.getUser().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User don't have access to file");
        }

        return uploadedFile.getStatus().getFileStatus();
    }

    /**
     * Lists the names of files accessible to the given user.
     *
     * @param user The user requesting the file list.
     * @return List of file names accessible to the user.
     * @throws ResponseStatusException If the user is not found.
     */
    @Transactional
    public List<String> listFiles(@Nullable User user) {
        Objects.requireNonNull(user, "User not found");

        // Check user role and retrieve corresponding files
        if (user.getRole().getId() == 3) {
            // For regular users, return their specific files
            return uploadedFileRepository.findByUser(user).stream().map(UploadedFile::getFileName).collect(Collectors.toList());
        } else {
            // For admin users, return all files
            List<String> fileNames = new ArrayList<>();
            for (UploadedFile file : uploadedFileRepository.findAll()) {
                fileNames.add(file.getFileName());
            }
            return fileNames;
        }
    }

    /**
     * Retrieves the contents of the specified uploaded file for the given user.
     *
     * @param fileId The ID of the uploaded file.
     * @param user   The user requesting the file contents.
     * @return List of GradeSheet entities representing the contents of the file.
     * @throws ResponseStatusException If the user doesn't have access to the file or the file is not found.
     */
    @Transactional
    public List<GradeSheet> fileContents(Integer fileId, User user) {
        Objects.requireNonNull(user, "User not found");
        UploadedFile uploadedFile = uploadedFileRepository.findById(fileId).orElse(null);
        if (uploadedFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not found");
        }

        // Check if user has access rights to the file
        if (user.getRole().getId() == 3 && Objects.equals(uploadedFile.getUser().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User don't have access to file");
        }

        // Update last access time and create FileAccessLog entry
        uploadedFile.setLastAccessTime(new Date());
        uploadedFileRepository.save(uploadedFile);

        // Log file access
        FileAccessLog fileAccessLog = new FileAccessLog();
        fileAccessLog.setFile(uploadedFile);
        fileAccessLog.setUser(user);
        fileAccessLog.setAccessTime(new Date());
        fileAccessLogRepository.save(fileAccessLog);

        // Retrieve and return the GradeSheet entities associated with the file
        return gradeSheetRepository.findByFile(uploadedFile);
    }

    /**
     * Deletes the specified uploaded file and its associated records from the database.
     *
     * @param fileId The ID of the uploaded file to be deleted.
     * @throws ResponseStatusException If the file is not found.
     */
    @Transactional
    public void deleteFile(Integer fileId) {
        // Retrieve the UploadedFile entity by fileId
        UploadedFile uploadedFile = uploadedFileRepository.findById(fileId).orElse(null);
        if (uploadedFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not found");
        }

        // Mark the file as deleting
        uploadedFile.setStatus(FileStatus.DELETING);
        uploadedFileRepository.save(uploadedFile);

        // Delete GradeSheet records associated with the file
        gradeSheetRepository.deleteByFile(uploadedFile);

        // Delete the UploadedFile entity
        uploadedFileRepository.delete(uploadedFile);
    }
}

