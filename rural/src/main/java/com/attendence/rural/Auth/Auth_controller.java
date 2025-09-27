package com.attendence.rural.Auth;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.DTos.School_dto;
import com.attendence.rural.DTos.Student_dto;
import com.attendence.rural.DTos.Teacher_dto;
import com.attendence.rural.Jwt.Teacher_jwt;
import com.attendence.rural.Model.Role;
import com.attendence.rural.Model.User;
import com.attendence.rural.RespDtos.School_Resp;
import com.attendence.rural.RespDtos.StudentResp;
import com.attendence.rural.RespDtos.Teache_Resp;
import com.attendence.rural.Service.School_Service;
import com.attendence.rural.Service.Student_Service;
import com.attendence.rural.Service.Teacher_AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class Auth_controller {

        private final AuthenticationManager authenticationManager;
         private final Teacher_jwt teacher_jwt;
         private final Student_Service student_Service;
         private final Teacher_AuthService teacher_AuthService;
         private final School_Service school_Service;

    public Auth_controller(AuthenticationManager authenticationManager, Teacher_jwt teacher_jwt,Student_Service student_Service,Teacher_AuthService teacher_AuthService,School_Service school_Service) {
        this.authenticationManager = authenticationManager;
        this.teacher_jwt = teacher_jwt;
        this.student_Service = student_Service;
        this.teacher_AuthService = teacher_AuthService;
        this.school_Service = school_Service;
    }

    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<?> login(@RequestBody Auth_request request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(request.username(), request.password())  
            );         
   
            //  Get authenticated user
            User user = (User) authentication.getPrincipal();

             // Extract roles
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

     @Operation(summary = "Create student", description = "Add a new student (requires school first)")
     @PostMapping("/student")
     @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResp> createStudent(@Valid @RequestBody Student_dto request) {
        StudentResp resp = student_Service.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    
    @Operation(summary = "Register teacher", description = "Create a new teacher account")
    @PostMapping("/Teacher")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Teache_Resp> register(@Valid
                                                @RequestBody Teacher_dto teacher_dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacher_AuthService.register(teacher_dto));
    }

    @Operation(summary = "Create school", description = "Add a new school to the system")
    @PostMapping("/school")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<School_Resp> createSchool(
                    @Valid @RequestBody
                        School_dto request
    ) {
        School_Resp school_Resp = school_Service.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(school_Resp);
    }

    
}
