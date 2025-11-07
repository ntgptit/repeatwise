package com.repeatwise.config;

import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final @NonNull MessageSource messageSource;

    @Bean
    LocalValidatorFactoryBean localValidatorFactoryBean() {
        final var validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setValidationMessageSource(Objects.requireNonNull(this.messageSource));
        return validatorFactoryBean;
    }

    @Override
    public Validator getValidator() {
        return localValidatorFactoryBean();
    }
}
