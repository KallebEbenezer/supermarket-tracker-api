package com.supermarkettracker.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    OpenAPI supermarketTrackerOpenApi() {
        return new OpenAPI().info(new Info().title("Supermarket Tracker API")
                .version("v1").description("API de gestão comercial do Supermarket Tracker"));
    }
}
