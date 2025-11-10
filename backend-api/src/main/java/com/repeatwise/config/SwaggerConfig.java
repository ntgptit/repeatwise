package com.repeatwise.config;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.repeatwise.config.properties.AppProperties;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final AppProperties appProperties;

    @Bean
    OpenAPI repeatwiseOpenApi() {
        final var info = this.appProperties;

        return new OpenAPI()
                .info(new Info()
                        .title(StringUtils.defaultIfBlank(info.getName(), "RepeatWise API"))
                        .version(StringUtils.defaultIfBlank(info.getVersion(), "1.0.0"))
                        .description(StringUtils.defaultIfBlank(info.getDescription(), "RepeatWise API documentation"))
                        .contact(new Contact()
                                .name("RepeatWise Team")
                                .url("https://repeatwise.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                        .termsOfService("https://repeatwise.com/terms"))
                .addServersItem(new Server()
                        .url("/api")
                        .description("Default API server"));
    }

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }

}
