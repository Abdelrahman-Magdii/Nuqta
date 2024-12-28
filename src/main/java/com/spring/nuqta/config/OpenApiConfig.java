package com.spring.nuqta.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Nuqta API",
                version = "1.0",
                description = "Online Blood Bank API"
        )
)
public class OpenApiConfig {

}
