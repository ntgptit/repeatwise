package com.repeatwise.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.repeatwise.config.properties.AppProperties;
import com.repeatwise.config.properties.JwtProperties;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, AppProperties.class})
public class ApplicationPropertiesConfig {
}

