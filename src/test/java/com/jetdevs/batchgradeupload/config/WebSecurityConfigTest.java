package com.jetdevs.batchgradeupload.config;

import com.jetdevs.batchgradeupload.service.CustomAuthenticationManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;


    @Test
    void testUnauthenticatedRequestShouldFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/authenticated"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testBasicAuthenticationWithInvalidCredentialsShouldFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/authenticated").with(httpBasic("invalidUsername", "invalidPassword")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCustomAuthenticationManagerIsAutowired() {
        assertNotNull(customAuthenticationManager);
    }
}
