package com.jetdevs.batchgradeupload.service;

import com.jetdevs.batchgradeupload.entity.GradeSheet;
import com.jetdevs.batchgradeupload.entity.UploadedFile;
import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.model.FileStatus;
import com.jetdevs.batchgradeupload.repository.GradeSheetRepository;
import com.jetdevs.batchgradeupload.repository.UploadedFileRepository;
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
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileUploadService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(5); // You can adjust the number of threads as needed
    Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    @Autowired
    private UploadedFileRepository uploadedFileRepository;
    @Autowired
    private GradeSheetRepository gradeSheetRepository;

    public Integer saveFile(MultipartFile file, Optional<User> userOptional) throws ResponseStatusException, IOException {
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        if (!file.getOriginalFilename().endsWith(".xls") && !file.getOriginalFilename().endsWith(".xlsx")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excel file required");
        }
        UploadedFile fileEntity = new UploadedFile();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFile(file.getBytes());
        fileEntity.setUser(userOptional.get());
        fileEntity.setUploadedTime(new Date());
        fileEntity.setStatus(FileStatus.Uploaded);
        uploadedFileRepository.save(fileEntity);
        processExcelFileAsync(fileEntity, file);
        logger.info("File save successful");
        return fileEntity.getId();
    }

    private void processExcelFileAsync(UploadedFile uploadedFile, MultipartFile multipartFile) {
        executorService.submit(() -> {
            try {
                logger.info("Converting file");
                // Read and process the Excel file using Apache POI
                Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream());

                processAndSaveDataFromExcel(workbook, uploadedFile);

                workbook.close();
                uploadedFile.setStatus(FileStatus.Dumped);
                logger.info("Converting Successful");
            } catch (Exception e) {
                uploadedFile.setStatus(FileStatus.Error);
                // Handle exceptions appropriately (log or throw further)
                logger.error("Error converting file", e);
            }
            uploadedFileRepository.save(uploadedFile);
        });
    }

    private void processAndSaveDataFromExcel(Workbook workbook, UploadedFile file) {
        Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

        List<GradeSheet> gradeSheetList = new ArrayList<>();

        // Iterate through rows starting from the second row (index 1)
        for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {

                int enrollmentNumber = (int) row.getCell(0).getNumericCellValue();
                int grade = (int) row.getCell(1).getNumericCellValue();
                String subject = row.getCell(2).getStringCellValue();

                // Create a new entity and save it to the database
                GradeSheet gradeSheet = new GradeSheet();
                gradeSheet.setFile(file);
                gradeSheet.setEnrollmentNumber(enrollmentNumber);
                gradeSheet.setGrade(grade);
                gradeSheet.setSubject(subject);
                gradeSheetList.add(gradeSheet);
            }
            gradeSheetRepository.saveAll(gradeSheetList);
        }
    }
}
