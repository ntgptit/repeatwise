package com.repeatwise.config;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Configuration for i18n message source.
 * Configures MessageSource bean to load localized messages from messages.properties.
 * Configures LocaleResolver to detect locale from Accept-Language request header.
 */
@Configuration
public class MessageSourceConfig {

    /**
     * Configure message source for internationalization.
     * Uses ReloadableResourceBundleMessageSource for better performance and reloading capability.
     *
     * Message files:
     * - messages.properties (English - default)
     * - messages_vi.properties (Vietnamese)
     */
    @Bean
    MessageSource messageSource() {
        final var messageSource = new ReloadableResourceBundleMessageSource();

        // Set basename (location of message files)
        messageSource.setBasename("classpath:messages");

        // Set encoding to UTF-8 to support Vietnamese characters
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());

        // Set default locale to English
        messageSource.setDefaultLocale(Locale.forLanguageTag("en"));

        // Don't fall back to system locale, use default locale instead
        messageSource.setFallbackToSystemLocale(false);

        // Cache messages for 1 hour (3600 seconds)
        messageSource.setCacheSeconds(3600);

        // Use message code as default message if not found
        messageSource.setUseCodeAsDefaultMessage(false);

        return messageSource;
    }

    /**
     * Locale resolver based on Accept-Language header.
     * Automatically detects locale from HTTP request header.
     *
     * Example:
     * - Accept-Language: en-US -> uses messages.properties
     * - Accept-Language: vi-VN -> uses messages_vi.properties
     */
    @Bean
    LocaleResolver localeResolver() {
        final var localeResolver = new AcceptHeaderLocaleResolver();

        // Set default locale to Vietnamese (for RepeatWise app)
        localeResolver.setDefaultLocale(Locale.forLanguageTag("vi"));

        // Set supported locales
        localeResolver.setSupportedLocales(Objects.requireNonNull(supportedLocales()));

        return localeResolver;
    }

    /**
     * Define supported locales for the application
     */
    private List<Locale> supportedLocales() {
        return List.of(
                Locale.forLanguageTag("vi"), // Vietnamese
                Locale.forLanguageTag("en")  // English
        );
    }
}
