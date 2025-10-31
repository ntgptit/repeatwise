package com.repeatwise.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Message Source Configuration
 *
 * Requirements:
 * - Coding Convention: MessageSource for error messages
 * - Internationalization (i18n) support
 *
 * @author RepeatWise Team
 */
@Configuration
public class MessageConfig {

    @Bean
    MessageSource messageSource() {
        final var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
