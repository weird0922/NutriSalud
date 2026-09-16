package com.nutriSalud.nutri.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "NutriSalud API",
                version = "v1",
                description = "Backend REST TP NutriSalud - Spring Boot 4 · JWT · Roles CIUDADANO / PERSONAL_SALUD · Soft Delete · JSend",
                contact = @Contact(name = "TP NutriSalud", url = "https://github.com/weird0922/NutriSalud")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor local dev")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Ingresa el token JWT SIN el prefijo 'Bearer ' (Swagger lo agrega automáticamente).\n" +
                "Obtén el token desde POST /api/v1/auth/login (admin:Nutri2026!  o  ciudadano1:Ciudadano2026!)."
)
public class OpenApiConfig {
}
