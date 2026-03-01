package com.attendence.rural.RespDtos;

public record PasswordResetResponse(
    String message,
    boolean success,
    String token
) {
    
}
