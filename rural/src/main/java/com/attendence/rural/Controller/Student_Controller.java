package com.attendence.rural.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.DTos.Student_dto;
import com.attendence.rural.RespDtos.StudentResp;
import com.attendence.rural.Service.Student_Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Student APIs", description = "Operations related to students")
public class Student_Controller {

    private final Student_Service student_Service;

    public Student_Controller(Student_Service student_Service){
        this.student_Service = student_Service;
    }
    

    @Operation(summary = "Create student", description = "Add a new student (requires school first)")
     @PostMapping("/register")
    public ResponseEntity<StudentResp> createStudent(@Valid @RequestBody Student_dto request) {
        StudentResp resp = student_Service.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Operation(summary = "Get student by roll number", description = "Retrieve a student’s details by roll number")
    @GetMapping("/{rollno}")
    public ResponseEntity<StudentResp> getStudentByRollno(@PathVariable int rollno) {
        StudentResp resp = student_Service.getStudentByRollno(rollno);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Get all students", description = "Retrieve all students")
    @GetMapping
    public ResponseEntity<List<StudentResp>> getAllStudents() {
        List<StudentResp> students = student_Service.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Delete student", description = "Remove a student by roll number")
    @DeleteMapping("/{rollno}")
    public ResponseEntity<Void> deleteStudent(@PathVariable int rollno) {
        student_Service.deleteStudent(rollno);
        return ResponseEntity.noContent().build(); 
    }
}
