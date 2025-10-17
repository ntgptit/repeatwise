package com.repeatwise.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing Configuration
 *
 * Requirements:
 * - Database Schema: Auto-populate created_at, updated_at fields
 * - BaseEntity: @CreatedDate, @LastModifiedDate support
 *
 * @author RepeatWise Team
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Enable JPA Auditing for @CreatedDate and @LastModifiedDate
}
