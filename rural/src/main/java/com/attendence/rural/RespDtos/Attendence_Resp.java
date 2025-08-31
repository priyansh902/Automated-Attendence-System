package com.attendence.rural.RespDtos;

import java.time.LocalDate;

public record Attendence_Resp(
     String studentName,
    String rollNumber,
    String className,
    String schoolName,
    LocalDate date,
    String status,
    boolean syncStatus
) {
    
}
