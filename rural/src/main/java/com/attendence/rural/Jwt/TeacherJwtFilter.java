package com.attendence.rural.Jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TeacherJwtFilter extends OncePerRequestFilter {

    private final Teacher_jwt teacher_jwt;
    private final UserDetailsService userDetailsService;

    public TeacherJwtFilter(Teacher_jwt teacher_jwt, UserDetailsService userDetailsService) {
        this.teacher_jwt = teacher_jwt;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. No Authorization header → let Spring handle (unauthenticated request)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // 2. Validate JWT format & expiration
        try {
            if (!teacher_jwt.isTokenValid(token)) {
                sendUnauthorizedResponse(response, "Token expired or invalid");
                return;
            }
        } catch (Exception e) {
            sendUnauthorizedResponse(response, "Invalid or malformed token");
            return;
        }

        // 3. Extract username
        String username = teacher_jwt.extractUsername(token);

        // 4. Skip if already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 🔑 Recommended: load from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (teacher_jwt.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                sendUnauthorizedResponse(response, "Token expired or invalid");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // Helper to send JSON 401
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
