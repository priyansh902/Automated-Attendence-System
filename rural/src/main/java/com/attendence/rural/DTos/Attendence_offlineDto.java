package com.attendence.rural.DTos;

import java.time.LocalDate;

import com.attendence.rural.Model.Status;

import jakarta.validation.constraints.NotNull;

public record Attendence_offlineDto(
    // @NotBlank 
    String uniquecode,
    String rfidTagId,
    @NotNull LocalDate date,
    @NotNull Status status

) {
    
}
