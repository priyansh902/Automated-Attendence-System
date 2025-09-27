package com.attendence.rural.DTos;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Attendance_bulkAtDto(
    @NotBlank String schoolName,
    @NotNull LocalDate date,
    List<Attendance_bulkDto> records
) {
    
}
