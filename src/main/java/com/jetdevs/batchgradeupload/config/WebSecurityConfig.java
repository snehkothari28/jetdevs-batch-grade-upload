package com.jetdevs.batchgradeupload.config;

import com.jetdevs.batchgradeupload.service.CustomAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class WebSecurityConfig {


    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        (auth) -> auth
                                .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .authenticationManager(customAuthenticationManager);
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
