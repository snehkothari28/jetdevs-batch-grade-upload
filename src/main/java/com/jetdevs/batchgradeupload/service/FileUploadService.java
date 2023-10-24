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

@Service
public class FileUploadService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(5); // You can adjust the number of threads as needed
    Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    @Autowired
    private UploadedFileRepository uploadedFileRepository;
    @Autowired
    private GradeSheetRepository gradeSheetRepository;
    @Autowired
    private FileAccessLogRepository fileAccessLogRepository;

    @Transactional
    public Integer saveFile(MultipartFile file, @Nullable User user) throws ResponseStatusException, IOException {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        if (!file.getOriginalFilename().endsWith(".xls") && !file.getOriginalFilename().endsWith(".xlsx")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excel file required");
        }
        UploadedFile fileEntity = new UploadedFile();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFile(file.getBytes());
        fileEntity.setUser(user);
        fileEntity.setUploadedTime(new Date());
        fileEntity.setLastAccessTime(new Date());
        fileEntity.setStatus(FileStatus.UPLOADED);
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

    @Transactional
    public String fileStatus(Integer fileId, @Nullable User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        UploadedFile uploadedFile = uploadedFileRepository.findById(fileId).orElse(null);
        if (uploadedFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not found");
        }

        //check if user have role user
        if (user.getRole().getId() == 3 && Objects.equals(uploadedFile.getUser().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User don't have access to file");
        }

        return uploadedFile.getStatus().getFileStatus();
    }

    @Transactional
    public List<String> listFiles(@Nullable User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        //check if user have role user
        if (user.getRole().getId() == 3) {
            return uploadedFileRepository.findByUser(user).stream().map(UploadedFile::getFileName).collect(Collectors.toList());
        }

        //for super admin and admin get all files
        List<String> fileNames = new ArrayList<>();
        for (UploadedFile file : uploadedFileRepository.findAll()) {
            fileNames.add(file.getFileName());
        }
        return fileNames;
    }

    @Transactional
    public List<GradeSheet> fileContents(Integer fileId, User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        UploadedFile uploadedFile = uploadedFileRepository.findById(fileId).orElse(null);
        if (uploadedFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not found");
        }


        //check if user have role user
        if (user.getRole().getId() == 3 && Objects.equals(uploadedFile.getUser().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User don't have access to file");
        }

        uploadedFile.setLastAccessTime(new Date());
        uploadedFileRepository.save(uploadedFile);

        FileAccessLog fileAccessLog = new FileAccessLog();
        fileAccessLog.setFile(uploadedFile);
        fileAccessLog.setUser(user);
        fileAccessLog.setAccessTime(new Date());
        fileAccessLogRepository.save(fileAccessLog);


        return gradeSheetRepository.findByFile(uploadedFile);
    }

    @Transactional
    public void deleteFile(Integer fileId) {

        UploadedFile uploadedFile = uploadedFileRepository.findById(fileId).orElse(null);
        if (uploadedFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not found");
        }

        uploadedFile.setStatus(FileStatus.DELETING);
        uploadedFileRepository.save(uploadedFile);

        logger.info("Deleting file records for file - {}", fileId);
        gradeSheetRepository.deleteByFile(uploadedFile);
        logger.info("Deleted file records for file - {}", fileId);


        logger.info("Deleting file - {}", fileId);
        uploadedFileRepository.delete(uploadedFile);
        logger.info("Deleted file - {}", fileId);

    }
}
