package com.attendence.rural.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

      @Bean
    public OpenAPI customOpenAPI() {
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
                )
                    .info(new Info()
                       .title("Automated Attendance System")   
                        .version("1.0.0")
                        .description("API documentation for the Automated Attendance System backend")
                    );
                    
    }
    
}
