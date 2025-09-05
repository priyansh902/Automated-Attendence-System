package com.attendence.rural.Jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.attendence.rural.Model.Teacher;
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
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
                final String authHeader = request.getHeader("Authorization");

            if(authHeader ==null || !authHeader.startsWith("Bearer ")){
                filterChain.doFilter(request, response);
                return;
           }

           String token = authHeader.substring(7);
           String username = teacher_jwt.extractUsername(token);

           if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Teacher teacher = teacher_Repo.findByUsername(username).orElse(null);

                if(teacher != null) {
                    UsernamePasswordAuthenticationToken authToken = 
                                            new UsernamePasswordAuthenticationToken(username, null, List.of());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                }
           }

           filterChain.doFilter(request, response);


    }


    
}
