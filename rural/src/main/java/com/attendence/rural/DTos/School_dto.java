package com.attendence.rural.DTos;

import jakarta.validation.constraints.NotBlank;

    public record School_dto(
        @NotBlank
        String name,

        @NotBlank
        String location
    ) {
        
    }
