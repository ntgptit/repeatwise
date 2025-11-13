package com.repeatwise.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Cấu hình thread pool cho job import/export.
 */
@Configuration
public class AsyncConfig {

    @Bean(name = "jobTaskExecutor")
    public Executor jobTaskExecutor() {
        final var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("async-job-");
        executor.initialize();
        return executor;
    }
}

