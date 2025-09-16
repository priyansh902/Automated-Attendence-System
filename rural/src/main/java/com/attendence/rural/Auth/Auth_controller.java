package com.attendence.rural.Auth;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.Jwt.Teacher_jwt;
import com.attendence.rural.Model.Role;
import com.attendence.rural.Model.User;

@RestController
@RequestMapping("/api/auth")
public class Auth_controller {

        private final AuthenticationManager authenticationManager;
         private final Teacher_jwt teacher_jwt;

    public Auth_controller(AuthenticationManager authenticationManager, Teacher_jwt teacher_jwt) {
        this.authenticationManager = authenticationManager;
        this.teacher_jwt = teacher_jwt;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Auth_request request) {
        try {
            // 1. Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(request.username(), request.password())  
            );         
   
            // 2. Get authenticated user
            User user = (User) authentication.getPrincipal();

             // 3️⃣ Extract roles
            List<String> roles = user.getRoles().stream()
                                     .map(Role::getName)
                                     .toList();

           String token = teacher_jwt.generateToken(user.getUsername(), roles);

           Auth_response response = new Auth_response(token,user.getUsername(), roles);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            // Handle invalid credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        } catch (Exception ex) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong"));
        }
    }
    
}
