package com.attendence.rural.DTos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

    public record Teacher_dto(
        @NotBlank(message = "name is required")
        String name,
        @NotBlank(message = "username is required")
        @Column(unique = true)
        String username,
        @NotBlank @Size(min = 6,message = "password must be at least 6 characters")
        String password,
        @NotBlank(message = "subject is required")
        String subject, 
        @NotBlank(message = "schoolname is required that is in the database")   
        String schoolName
    ) {
        
    }
