package com.attendence.rural.DTos;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Attendence_bulkAtDto(
    @NotBlank String schoolName,
    @NotNull LocalDate date,
    List<Attendence_bulkDto> records
) {
    
}
