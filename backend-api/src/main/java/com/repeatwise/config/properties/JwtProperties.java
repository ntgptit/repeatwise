package com.repeatwise.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private int accessTokenExpirationMinutes;
    private int refreshTokenExpirationDays;
    private String issuer;
    private String audience;
}
