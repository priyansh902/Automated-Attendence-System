package com.attendence.rural.DTos;

import jakarta.validation.constraints.NotBlank;

public record Login_dto(
    @NotBlank
    String username,
    @NotBlank
    String password
) {
    

}
