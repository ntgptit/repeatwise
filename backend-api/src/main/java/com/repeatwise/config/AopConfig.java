package com.repeatwise.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration for Spring AOP.
 * Enables AspectJ auto-proxying for logging and performance monitoring.
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
    // Spring Boot auto-configures AOP when spring-boot-starter-aop is present
    // This class exists for explicit configuration and documentation
}
