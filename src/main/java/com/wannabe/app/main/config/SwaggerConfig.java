package com.wannabe.app.main.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {

    private final String moduleName;
    private final String apiVersion;
    private final String description;
    private final String servicePath;

    public SwaggerConfig(
    @Value("${spring.application.name}") String moduleName,
    @Value("${spring.application.version}") String apiVersion,
    @Value("${spring.application.desc}") String desc,
    @Value("${service-path}") String servicePath
    ) {
        this.description = desc;
        this.moduleName = moduleName;
        this.apiVersion = apiVersion;
        this.servicePath = servicePath;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Authentication";
        final String apiTitle = String.format("%s API", StringUtils.capitalize(moduleName));
        Server server = new Server().url(servicePath);
        return new OpenAPI()
            .servers(Arrays.asList(server))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(
                new Components()
                    .addSecuritySchemes(securitySchemeName,
                        new io.swagger.v3.oas.models.security.SecurityScheme()
                            .name(securitySchemeName)
                            .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                        )
            )
            .info(new io.swagger.v3.oas.models.info.Info().title(apiTitle).version(apiVersion)
            .description(description));
    }
}
