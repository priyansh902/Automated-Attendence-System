package com.attendence.rural.RespDtos;


import java.time.LocalDate;

import com.attendence.rural.Model.Status;

public record Attendence_Resp(
     String studentName,
    int rollNumber,
    String className,
    String schoolName,
    LocalDate date,
     Status status,
    boolean syncStatus
) {
    
}
