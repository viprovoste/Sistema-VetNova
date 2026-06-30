package com.vetnova.agendamiento_sucursales.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI agendamientoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Agendamiento y Sucursales - VetNova")
                        .description("Microservicio de gestión de sucursales, boxes y horas médicas")
                        .version("1.0"));
    }
}