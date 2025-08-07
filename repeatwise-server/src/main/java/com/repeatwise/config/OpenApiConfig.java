package com.repeatwise.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RepeatWise API")
                .description("A Spaced Repetition System API for effective learning and knowledge retention")
                .version("1.0.0")
                .contact(new Contact()
                    .name("RepeatWise Team")
                    .email("support@repeatwise.com")
                    .url("https://repeatwise.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")));
    }
} 
