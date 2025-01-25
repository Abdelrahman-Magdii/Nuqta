package com.spring.nuqta.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
//@SecurityScheme(
//        name = "BearerAuth",
//        description = "JWT authentication",
//        scheme = "bearer",
//        type = SecuritySchemeType.HTTP,
//        bearerFormat = "JWT",
//        in = SecuritySchemeIn.HEADER
//)
@OpenAPIDefinition(
        info = @Info(
                title = "Nuqta API",
                version = "1.0",
                description = "Online Blood Bank API"
        )
)
public class SwaggerConfig {


}
