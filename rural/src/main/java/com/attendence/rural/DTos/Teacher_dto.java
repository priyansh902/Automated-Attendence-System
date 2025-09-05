package com.attendence.rural.DTos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Teacher_dto(
    @NotBlank
     String name,
     @NotBlank
    String username,
    @NotBlank @Size(min = 6,message = "password must be at least 6 characters")
    String password,
    @NotBlank
    String subject, 
    @NotBlank   
    String schoolName
) {
    
}
