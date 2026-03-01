package com.attendence.rural.RespDtos;

import java.time.LocalDate;

public record Holiday_Resp(
     Integer id,
    LocalDate date,
    String description,
    boolean active,
    LocalDate createdAt
) {
    
}
