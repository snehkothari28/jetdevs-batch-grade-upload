package com.jetdevs.batchgradeupload.service;

import com.jetdevs.batchgradeupload.entity.Role;
import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomAuthenticationManagerTest {

    @Mock
    private UserRepository userRepository;


    @Mock
    private PasswordEncoder encoder;

    @Mock
    private Logger logger;

    @InjectMocks
    private CustomAuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testSuccessfulAuthentication() {
        String username = "testuser";
        String password = "testpassword";
        User mockUser = new User();
        mockUser.setName(username);
        mockUser.setHashedPassword(encoder.encode(password));
        Role mockRole = new Role();
        mockRole.setName("ROLE_USER");
        mockUser.setRole(mockRole);

        when(userRepository.findByName(username)).thenReturn(java.util.Optional.of(mockUser));
        when(encoder.matches(password, mockUser.getHashedPassword())).thenReturn(true);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        assert authentication.isAuthenticated();
    }

    @Test
    void testAuthenticationWithInvalidUsername() {
        String username = "nonexistentuser";
        String password = "testpassword";

        when(userRepository.findByName(username)).thenReturn(java.util.Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)));
    }

    @Test
    void testAuthenticationWithInvalidPassword() {
        String username = "testuser";
        String password = "invalidpassword";
        User mockUser = new User();
        mockUser.setName(username);
        mockUser.setHashedPassword(encoder.encode("testpassword"));
        Role mockRole = new Role();
        mockRole.setName("ROLE_USER");
        mockUser.setRole(mockRole);

        when(userRepository.findByName(username)).thenReturn(java.util.Optional.of(mockUser));
        when(encoder.matches(password, mockUser.getHashedPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)));
    }
}
