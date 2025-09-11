package com.attendence.rural.DTos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

    public record Student_dto(

        @NotBlank
        String name,
        
        @Positive
        int rollno,

        @NotBlank
        String classname,

        // @NotBlank
        // String uniquecode,

        // @NotBlank
        // String rfidTagId,

        @NotBlank
        String schoolname
    ) {
        
    
        
    }
