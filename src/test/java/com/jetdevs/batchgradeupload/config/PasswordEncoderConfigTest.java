package com.jetdevs.batchgradeupload.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class PasswordEncoderConfigTest {

    private static final String SSHA512_PREFIX = "{SSHA-512}";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordEncoderNotNull() {
        assertNotNull(passwordEncoder);
    }

    @Test
    void testPasswordEncoderAlgorithm() {
        // Test if the default algorithm is set to "SSHA-512"
        String encodedPassword = extractEncodedPassword(passwordEncoder.encode("password"));
        assertTrue(passwordEncoder.matches("password", encodedPassword));
    }

    private String extractEncodedPassword(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf(SSHA512_PREFIX);
        return prefixEncodedPassword.substring(start + SSHA512_PREFIX.length());
    }
}
