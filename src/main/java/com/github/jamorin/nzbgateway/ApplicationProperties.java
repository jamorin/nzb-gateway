package com.github.jamorin.nzbgateway;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Configuration
@Validated
@ConfigurationProperties(prefix = "gateway", ignoreUnknownFields = false)
public class ApplicationProperties {

    @URL
    @NotNull
    private String baseUrl;

    @URL
    @NotNull
    private String auth0RolesNamespace;
}
