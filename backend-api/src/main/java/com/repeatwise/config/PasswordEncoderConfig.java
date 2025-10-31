package com.repeatwise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Encoder Configuration
 *
 * Requirements:
 * - UC-001: User Registration - Password hashing with bcrypt cost 12
 * - Security: Never store plain text passwords
 *
 * @author RepeatWise Team
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * BCrypt password encoder bean with cost factor 12
     *
     * Cost factor 12 provides good security while maintaining reasonable performance
     * Higher cost = more secure but slower
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
