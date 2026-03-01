package com.attendence.rural.Auth;

import java.util.List;

public record Auth_response(
    String token,
    String username,
    List<String> roles
) {
    
}
