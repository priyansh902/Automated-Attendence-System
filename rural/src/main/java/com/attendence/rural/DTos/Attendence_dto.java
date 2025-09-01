package com.attendence.rural.DTos;

import java.time.LocalDate;

import com.attendence.rural.Model.Status;



public record Attendence_dto(
    String studentName,
    int  rollNumber,   
    LocalDate date,
    Status status,     
    boolean syncStatus
) {
    
}
