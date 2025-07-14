package com.justteam.test_quest_api.swagger;


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme apiKey = new SecurityScheme()
         .type(SecurityScheme.Type.HTTP)
         .in(SecurityScheme.In.HEADER)
         .name("Authorization")
         .scheme("bearer")
        .bearerFormat("JWT");

        Info info = new Info().title("Test Quest Board API Document")
        .version("v0.0.1")
                .description("TestQuest API 명세서입니다.");
        return new OpenAPI()
            .components(new Components().addSecuritySchemes("BearerAuth", apiKey))
                .info(info).servers(List.of());
    }
    
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("TestQuest API")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(nullableFieldOpenApiCustomizer())
                .build();
    }
    
    @Bean
    public OpenApiCustomizer nullableFieldOpenApiCustomizer() {
        return openApi -> {
            openApi.getComponents().getSchemas().values().forEach(schema -> {
                if (schema.getProperties() != null) {
                    schema.getProperties().forEach((propertyName, property) -> {
                        if (schema.getRequired() == null || !schema.getRequired().contains(propertyName)) {
                            if (property instanceof Schema) {
                                ((Schema<?>) property).setNullable(true);
                            }
                        }
                    });
                }
            });
        };
    }
}
