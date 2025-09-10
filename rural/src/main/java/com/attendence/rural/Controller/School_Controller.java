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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/schools")
@Tag(name = "School APIs", description = "Operation related to School Apis")
public class School_Controller {

    private final School_Service school_Service;

    public School_Controller(School_Service school_Service){
        this.school_Service = school_Service;
    }
    
    @Operation(summary = "Create school", description = "Add a new school to the system")
    @PostMapping
    public ResponseEntity<School_Resp> createSchool(
                    @Valid @RequestBody
                        School_dto request
    ) {
        School_Resp school_Resp = school_Service.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(school_Resp);
    }

    @Operation(summary = "Get school by name", description = "Retrieve school details by name")
    @GetMapping("/{name}")
    public ResponseEntity<School_Resp> getSchool(
                                @PathVariable 
                                    String name ) {
            
        return ResponseEntity.ok(school_Service.getSchoolByName(name));
                                 
    }

    @Operation(summary = "Get all schools", description = "Retrieve all schools")
    @GetMapping
    public ResponseEntity<List<School_Resp>> getAllSchools() {
        return ResponseEntity.ok(school_Service.getAllSchools());
    }

    @Operation(summary = "Delete school", description = "Remove a school by name")
    @DeleteMapping
    public  ResponseEntity<Void> deleteSchool (
                                        @PathVariable String name
                  ) {

                    school_Service.deleteSchool(name);
                    return ResponseEntity.noContent().build();
    }

}
