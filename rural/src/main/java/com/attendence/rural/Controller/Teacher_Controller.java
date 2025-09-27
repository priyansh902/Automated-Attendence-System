package com.attendence.rural.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.RespDtos.Teache_Resp;
import com.attendence.rural.Service.Teacher_AuthService;
import com.attendence.rural.Service.Teacher_Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/teachers")
@Tag(name = "Teacher APIs", description = "Operations related to teachers")
public class Teacher_Controller {

    private final Teacher_Service teacher_Service;

    private final Teacher_AuthService teacher_AuthService;

    public Teacher_Controller(Teacher_Service teacher_Service, Teacher_AuthService teacher_AuthService){
        this.teacher_AuthService = teacher_AuthService;
        this.teacher_Service = teacher_Service;
    }


  

    @Operation(summary = "Get teacher by username", description = "Retrieve teacher details")
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Teache_Resp> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(teacher_Service.getTeacherByusername(username));
    }

    @Operation(summary = "Get all teachers", description = "Retrieve all teachers")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Teache_Resp>> getAll() {
        return ResponseEntity.ok(teacher_Service.getAllTeachers());
    }

    @Operation(summary = "Delete teacher", description = "Remove teacher account by username")
     @DeleteMapping("/{username}")
     @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String username) {
        teacher_Service.deleteTeacher(username);
        return ResponseEntity.noContent().build();
    }
    
}
