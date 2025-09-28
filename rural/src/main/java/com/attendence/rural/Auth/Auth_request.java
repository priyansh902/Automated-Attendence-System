package com.attendence.rural.Auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record Auth_request(
    @Column(unique = true)
    @NotBlank
    String username,
    @Column(unique = true)
    @NotBlank
    String password
) {
    
}
