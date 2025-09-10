package com.attendence.rural;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class Sih12Application {

	public static void main(String[] args) {
		SpringApplication.run(Sih12Application.class, args);
	}

}

// @Configuration
//  class SwaggerConfig {

//     @Bean
//     public OpenAPI customOpenAPI() {
//         return new OpenAPI()
// 			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
// 				.components(new Components()
// 					.addSecuritySchemes("bearerAuth",
// 						new SecurityScheme()
// 							.name("bearerAuth")
// 								.type(SecurityScheme.Type.HTTP)
// 									.scheme("bearer")
// 										.bearerFormat("JWT") 
// 					)	
// 				);
//             // .info(new Info()
//             //     .title("E-Learning API")
//             //     .version("1.0")
//             //     .description("API documentation for Automated Attendence System Application"));
//     }
// }

