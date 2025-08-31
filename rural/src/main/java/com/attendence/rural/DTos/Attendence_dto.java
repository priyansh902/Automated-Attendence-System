package com.attendence.rural.DTos;

import java.time.LocalDate;

public record Attendence_dto(
    String studentName,
    String rollNumber,   
    LocalDate date,
    String status,     
    boolean syncStatus
) {
    
}
