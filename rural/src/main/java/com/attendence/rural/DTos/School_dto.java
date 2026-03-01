package com.attendence.rural.DTos;

import jakarta.validation.constraints.NotBlank;

    public record School_dto(
        @NotBlank(message = "name is required")
        String name,

        @NotBlank(message = "location is required")
        String location
    ) {
        
    }
