package com.repeatwise.config;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Configuration for i18n message source
 */
@Configuration
public class MessageSourceConfig {

    /**
     * Configure message source for internationalization
     */
    @Bean
    MessageSource messageSource() {
        final var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setDefaultLocale(Locale.forLanguageTag("en"));
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setCacheSeconds(3600); // Cache for 1 hour
        return messageSource;
    }

    /**
     * Locale resolver based on Accept-Language header
     */
    @Bean
    LocaleResolver localeResolver() {
        final var localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.forLanguageTag("en"));
        localeResolver.setSupportedLocales(Objects.requireNonNull(supportedLocales()));
        return localeResolver;
    }

    private List<Locale> supportedLocales() {
        return List.of(
                Locale.forLanguageTag("vi"),
                Locale.forLanguageTag("en"));
    }
}
