package com.jetdevs.batchgradeupload.config;

import com.jetdevs.batchgradeupload.service.CustomAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuration class for setting up web security configurations.
 */
@Configuration
public class WebSecurityConfig {

    // Whitelist of URLs that do not require authentication
    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;

    /**
     * Configures the security filter chain.
     *
     * @param http HttpSecurity object for configuring web-based security for specific http requests.
     * @return SecurityFilterChain object defining the security filter chain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configuring authorization rules: allow access to AUTH_WHITELIST, authenticate all other requests.
        http
                .authorizeHttpRequests(
                        (auth) -> auth
                                .requestMatchers(AUTH_WHITELIST).permitAll()
                                .anyRequest().authenticated()
                )
                // Use HTTP basic authentication with default settings.
                .httpBasic(withDefaults())
                // Set the custom authentication manager.
                .authenticationManager(customAuthenticationManager);

        // Disable CSRF protection for this configuration.
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
