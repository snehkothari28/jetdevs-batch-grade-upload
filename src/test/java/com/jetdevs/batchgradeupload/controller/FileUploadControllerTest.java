package com.jetdevs.batchgradeupload.controller;

import com.jetdevs.batchgradeupload.entity.GradeSheet;
import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.repository.UserRepository;
import com.jetdevs.batchgradeupload.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class FileUploadControllerTest {

    @InjectMocks
    private FileUploadController fileUploadController;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("testUser");
    }

    @Test
    void testUploadFile() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(mock(User.class)));
        when(fileUploadService.saveFile(any(MultipartFile.class), any(User.class))).thenReturn(1);

        Integer fileId = fileUploadController.uploadFile(file);

        assertEquals(1, fileId.intValue());
    }

    @Test
    public void testFileStatus() {
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(mock(User.class)));
        when(fileUploadService.fileStatus(anyInt(), any(User.class))).thenReturn("Status");

        String status = fileUploadController.fileStatus(1);

        assertEquals("Status", status);
    }

    @Test
    public void testListFiles() {
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(mock(User.class)));
        when(fileUploadService.listFiles(any(User.class))).thenReturn(Arrays.asList("file1", "file2"));

        List<String> files = fileUploadController.listFiles();

        assertEquals(2, files.size());
        assertTrue(files.contains("file1"));
        assertTrue(files.contains("file2"));
    }

    @Test
    public void testFileContents() {
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(mock(User.class)));
        when(fileUploadService.fileContents(anyInt(), any(User.class))).thenReturn(Arrays.asList(mock(GradeSheet.class)));

        List<GradeSheet> gradeSheets = fileUploadController.fileContents(1);

        assertEquals(1, gradeSheets.size());
    }

    @Test
    public void testDeleteFile() {
        fileUploadController.deleteFile(1);

        verify(fileUploadService, times(1)).deleteFile(1);
    }
}
