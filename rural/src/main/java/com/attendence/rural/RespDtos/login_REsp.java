package com.attendence.rural.RespDtos;

import java.util.List;

public record login_REsp(
    String token,
    String username,
    List<String> roles
) {
    
}
