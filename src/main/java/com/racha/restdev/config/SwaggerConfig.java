package com.racha.restdev.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Demo API",
        version = "Versions 1.0",
        contact = @Contact(
            name = "rachadev", email = "rachadev032@gmail.com", url="/"
        ),
        description = "Spring boot restfull api demo by racha"
    )
)
public class SwaggerConfig {
    
}
