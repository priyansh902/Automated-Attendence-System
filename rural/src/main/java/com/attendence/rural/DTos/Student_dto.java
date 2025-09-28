package com.attendence.rural.DTos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

    public record Student_dto(

        @NotBlank(message = "name is required")
        String name,
        
        @Positive
        int rollno,

        @NotBlank(message = "classs name is required")
        String classname,

        // @NotBlank
        // String uniquecode,

        // @NotBlank
        // String rfidTagId,

        @NotBlank
        String schoolname,

        @NotBlank(message = "username is required & must be unique")
        @Column(unique = true) String username,

        @NotBlank(message = "passsword is required ")
         String password
    ) {
        
    
        
    }
