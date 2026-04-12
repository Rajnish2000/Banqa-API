package com.rajnish.bankmanagementRESTAPI.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;

@OpenAPIDefinition(
        info = @Info(
                title = "Banking Management API",
                version = "1.0",
                description = "API documentation for managing Banking System"
        )
)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }

    @Bean
    public OpenAPI baseOpenAPI(){
        return new OpenAPI()
                .components(new Components()
                        .addResponses("successfulResponse",
                                new ApiResponse().description("Request successful")
                        )
                        .addResponses("badRequest",
                                new ApiResponse().description("Bad request")
                        )
                        .addResponses("internalServerError",
                                new ApiResponse().description("Internal server error")
                        )
                );
    }
}
