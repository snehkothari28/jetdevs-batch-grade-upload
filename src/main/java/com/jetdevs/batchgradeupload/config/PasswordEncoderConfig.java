package com.jetdevs.batchgradeupload.config;

import com.jetdevs.batchgradeupload.util.Hmac512PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class responsible for configuring the password encoders used in the application.
 */
@Configuration
public class PasswordEncoderConfig {

    @Value("${password.salt}")
    private String salt;

    /**
     * Bean method to configure the password encoder.
     *
     * @return PasswordEncoder configured with multiple algorithms including SSHA-512 and bcrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Create a map to hold multiple password encoders.
        Map<String, PasswordEncoder> encoders = new HashMap<>();

        // Add custom Hmac512PasswordEncoder with the provided salt for SSHA-512 hashing.
        encoders.put("SSHA-512", new Hmac512PasswordEncoder(salt));

        // Add the BCryptPasswordEncoder for bcrypt hashing.
        encoders.put("bcrypt", new BCryptPasswordEncoder());

        // Create a DelegatingPasswordEncoder with SSHA-512 as the default encoding algorithm and supported encoders map.
        return new DelegatingPasswordEncoder("SSHA-512", encoders);
    }
}
