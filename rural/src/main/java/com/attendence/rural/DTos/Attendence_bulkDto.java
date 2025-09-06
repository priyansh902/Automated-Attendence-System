package com.attendence.rural.DTos;

import com.attendence.rural.Model.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Attendence_bulkDto(
     @NotBlank String uniquecode,
     @NotNull Status status
) {
    
}
