package com.springboot.config;

// Java
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authorization");
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))  // 기본 인증 적용
                .components(new Components().addSecuritySchemes("Authorization", new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .addSecurityItem(securityRequirement)
                .info(apiInfo());
    }


    private Info apiInfo() {
        return new Info()
                .title("구독매니아 API 문서")
                .description("구독 매니아 Swagger API 문서")
                .version("1.0.0");
    }
}