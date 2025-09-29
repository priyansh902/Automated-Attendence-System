package com.attendence.rural.DTos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HolidayDto(
     @NotNull(message = "Date is required")
    LocalDate date,
    
    @NotBlank(message = "Description is required")
    String description
) {
    
}
