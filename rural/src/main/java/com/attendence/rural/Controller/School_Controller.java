package com.attendence.rural.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.RespDtos.School_Resp;
import com.attendence.rural.Service.School_Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/schools")
@Tag(name = "School APIs", description = "Operation related to School Apis")
public class School_Controller {

    private final School_Service school_Service;

    public School_Controller(School_Service school_Service){
        this.school_Service = school_Service;
    }
    
    

    @Operation(summary = "Get school by name", description = "Retrieve school details by name")
    @GetMapping("/{name}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<School_Resp> getSchool(
                                @PathVariable 
                                    String name ) {
            
        return ResponseEntity.ok(school_Service.getSchoolByName(name));
                                 
    }

    @Operation(summary = "Get all schools", description = "Retrieve all schools")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<List<School_Resp>> getAllSchools() {
        return ResponseEntity.ok(school_Service.getAllSchools());
    }

    @Operation(summary = "Delete school", description = "Remove a school by name")
    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<Void> deleteSchool (
                                        @PathVariable String name
                  ) {

                    school_Service.deleteSchool(name);
                    return ResponseEntity.noContent().build();
    }

}
