package com.attendence.rural.Security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.attendence.rural.Jwt.Teacher_JwtFilter;

@Configuration
@EnableWebSecurity
public class Teacher_securityConfig {

    private final Teacher_JwtFilter teacher_JwtFilter;

    public Teacher_securityConfig (Teacher_JwtFilter teacher_JwtFilter){
        this.teacher_JwtFilter = teacher_JwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean 
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable) //csrf not need for jwt
                .cors(cors -> cors.configurationSource(configurationSource()))
                    .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/teachers/register", "/api/teachers/login"
                            , "/swagger-ui/**",
                                "/v3/api-docs/**",
                                     "/swagger-resources/**",
                                         "/webjars/**",
                                              "/attendance-api.yaml"
                        ).permitAll()
                            .anyRequest().authenticated()             )
                                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                    .addFilterBefore(teacher_JwtFilter, UsernamePasswordAuthenticationFilter.class)
                                        .build();
    }

    @Bean
    public CorsConfigurationSource configurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("*"));  //change later for react front end
                 configuration.setAllowedMethods(List.of("Get","Post","Put","Delete"));
                     configuration.setAllowedHeaders(List.of("*")); // allow all headers
                         configuration.setExposedHeaders(List.of("Authorization")); // so frontend can read token
                             configuration.setAllowCredentials(true); // if you ever use cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    }
    
}
