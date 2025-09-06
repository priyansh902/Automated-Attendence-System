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

import com.attendence.rural.DTos.School_dto;
import com.attendence.rural.RespDtos.School_Resp;
import com.attendence.rural.Service.School_Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/schools")
public class School_Controller {

    private final School_Service school_Service;

    public School_Controller(School_Service school_Service){
        this.school_Service = school_Service;
    }
    
    // Create School in the database
    @PostMapping
    public ResponseEntity<School_Resp> createSchool(
                    @Valid @RequestBody
                        School_dto request
    ) {
        School_Resp school_Resp = school_Service.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(school_Resp);
    }

    // Get School By name in the database
    @GetMapping("/{name}")
    public ResponseEntity<School_Resp> getSchool(
                                @PathVariable 
                                    String name ) {
            
        return ResponseEntity.ok(school_Service.getSchoolByName(name));
                                 
    }

    // Return list of School in the database
    @GetMapping
    public ResponseEntity<List<School_Resp>> getAllSchools() {
        return ResponseEntity.ok(school_Service.getAllSchools());
    }

    // Delete Schooldata in to the database
    @DeleteMapping
    public  ResponseEntity<Void> deleteSchool (
                                        @PathVariable String name
                  ) {

                    school_Service.deleteSchool(name);
                    return ResponseEntity.noContent().build();
    }

}
