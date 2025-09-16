package com.attendence.rural.Jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.attendence.rural.Repositor.Teacher_Repo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class Teacher_JwtFilter extends OncePerRequestFilter {

    private final Teacher_jwt teacher_jwt;

    private final Teacher_Repo teacher_Repo;

    public Teacher_JwtFilter(Teacher_jwt teacher_jwt, Teacher_Repo teacher_Repo){
        this.teacher_Repo= teacher_Repo;
        this.teacher_jwt = teacher_jwt;
    }


     @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        //  Validate JWT token
        if (!teacher_jwt.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract username and roles from JWT
        String username = teacher_jwt.extractUsername(token);
        List<String> roles = teacher_jwt.extractRoles(token);

        // Set authentication only if not already set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Convert roles to GrantedAuthority
            var authorities = roles.stream()
                                   .map(SimpleGrantedAuthority::new)
                                   .toList();

            // Create Authentication token
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
    
}
