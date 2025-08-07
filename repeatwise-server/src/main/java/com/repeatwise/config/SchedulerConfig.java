package com.repeatwise.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    // This configuration enables Spring's scheduling capabilities
    // All @Scheduled annotations will be processed
} 
