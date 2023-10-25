 package com.jetdevs.batchgradeupload.service;

import com.jetdevs.batchgradeupload.entity.GradeSheet;
import com.jetdevs.batchgradeupload.entity.Role;
import com.jetdevs.batchgradeupload.entity.UploadedFile;
import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.model.FileStatus;
import com.jetdevs.batchgradeupload.repository.FileAccessLogRepository;
import com.jetdevs.batchgradeupload.repository.GradeSheetRepository;
import com.jetdevs.batchgradeupload.repository.UploadedFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FileUploadServiceTest {

    @Mock
    private UploadedFileRepository uploadedFileRepository;

    @Mock
    private GradeSheetRepository gradeSheetRepository;

    @Mock
    private FileAccessLogRepository fileAccessLogRepository;

    @InjectMocks
    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileUploadService = new FileUploadService(uploadedFileRepository, gradeSheetRepository, fileAccessLogRepository);
    }

    @Test
    void testSaveFile() throws IOException {
        MultipartFile multipartFile = null;
        User user = new User();
        user.setId(1);

        when(uploadedFileRepository.save(any())).thenReturn(new UploadedFile());

        assertThrows(NullPointerException.class, () -> fileUploadService.saveFile(multipartFile, user));
    }

    @Test
    void testFileStatus() {
        User user = new User();
        user.setId(1);
        Role role = new Role();
        role.setId(2);
        user.setRole(role);
        int fileId = 1;

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setStatus(FileStatus.UPLOADED);
        when(uploadedFileRepository.findById(fileId)).thenReturn(java.util.Optional.of(uploadedFile));
        when(fileAccessLogRepository.save(any())).thenReturn(null);

        String status = fileUploadService.fileStatus(fileId, user);
        assertEquals(FileStatus.UPLOADED.getFileStatus(), status);
    }

    @Test
    void testFileContents() {
        User user = new User();
        user.setId(1);
        Role role = new Role();
        role.setId(2);
        user.setRole(role);
        int fileId = 1;

        when(uploadedFileRepository.findById(fileId)).thenReturn(java.util.Optional.of(new UploadedFile()));
        when(gradeSheetRepository.findByFile(any())).thenReturn(List.of());

        List<GradeSheet> gradeSheets = fileUploadService.fileContents(fileId, user);
        assertNotNull(gradeSheets);
        assertEquals(0, gradeSheets.size());
    }

    @Test
    void testDeleteFile() {
        int fileId = 1;

        when(uploadedFileRepository.findById(fileId)).thenReturn(java.util.Optional.of(new UploadedFile()));

        assertDoesNotThrow(() -> fileUploadService.deleteFile(fileId));
    }

    @Test
    void testDeleteFileWithNonexistentFile() {
        int fileId = 1;

        when(uploadedFileRepository.findById(fileId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class, () -> fileUploadService.deleteFile(fileId));
    }
}
