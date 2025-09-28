package com.attendence.rural.DTos;

import jakarta.validation.constraints.NotBlank;

public record ForgetPasswordRequest(
    @NotBlank(message = "username is required")
    String username
) {
    
}
