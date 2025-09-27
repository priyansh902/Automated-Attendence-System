package com.attendence.rural.DTos;

import jakarta.validation.constraints.NotBlank;

public record Attendance_rfidDto(
   @NotBlank String rfidTagId
) {
    
}
