package com.attendence.rural.Jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.attendence.rural.Model.Device;
import com.attendence.rural.Repositor.Device_Repo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DeviceApiKeyFilter  extends OncePerRequestFilter {

    private final Device_Repo deviceRepository;

    public DeviceApiKeyFilter(Device_Repo deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Only check for API keys on /api/attendance/rfid endpoints
        if (request.getRequestURI().startsWith("/api/attendence/rfid")) {

            if (authHeader == null || !authHeader.startsWith("ApiKey ")) {
                sendUnauthorized(response, "Missing API Key");
                return;
            }

            String apiKey = authHeader.substring(7);

            var deviceOpt = deviceRepository.findByApiKeyAndActiveTrue(apiKey);
            if (deviceOpt.isEmpty()) {
                sendUnauthorized(response, "Invalid or inactive device API key");
                return;
            }

            // Mark request as authenticated with device principal
            Device device = deviceOpt.get();
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            device.getDeviceId(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_DEVICE"))
                    );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}