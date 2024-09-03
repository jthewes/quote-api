package de.zedalite.quotes.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
  info = @Info(
    title = "Quote API",
    version = "1.16.0",
    description = "A RESTful API acting as a link between the frontend and the datasource"
  ),
  security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
  name = "bearerAuth",
  description = "JWT authentication",
  scheme = "bearer",
  type = SecuritySchemeType.HTTP,
  bearerFormat = "JWT"
)
public class OpenApiConfig {}
