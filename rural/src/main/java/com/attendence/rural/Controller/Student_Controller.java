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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/students")

public class Student_Controller {

    private final Student_Service student_Service;

    public Student_Controller(Student_Service student_Service){
        this.student_Service = student_Service;
    }
    

    //  Create student in the database but first create school
     @PostMapping
    public ResponseEntity<StudentResp> createStudent(@Valid @RequestBody Student_dto request) {
        StudentResp resp = student_Service.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // GET Student by roll number in the database
    @GetMapping("/{rollno}")
    public ResponseEntity<StudentResp> getStudentByRollno(@PathVariable int rollno) {
        StudentResp resp = student_Service.getStudentByRollno(rollno);
        return ResponseEntity.ok(resp);
    }

    // return list of Students in the database
    @GetMapping
    public ResponseEntity<List<StudentResp>> getAllStudents() {
        List<StudentResp> students = student_Service.getAllStudents();
        return ResponseEntity.ok(students);
    }

    // DELETE Student data by roll number in the database
    @DeleteMapping("/{rollno}")
    public ResponseEntity<Void> deleteStudent(@PathVariable int rollno) {
        student_Service.deleteStudent(rollno);
        return ResponseEntity.noContent().build(); 
    }
}
