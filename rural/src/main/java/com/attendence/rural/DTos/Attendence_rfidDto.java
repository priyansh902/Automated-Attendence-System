package com.attendence.rural.DTos;

import jakarta.validation.constraints.NotBlank;

public record Attendence_rfidDto(
   @NotBlank String rfidTagId
) {
    
}
