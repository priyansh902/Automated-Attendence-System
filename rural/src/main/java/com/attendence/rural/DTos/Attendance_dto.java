package com.attendence.rural.DTos;

import java.time.LocalDate;

import com.attendence.rural.Model.Status;



public record Attendance_dto(
    String studentName,
    int  rollno,   
    LocalDate date,
    Status status,     
    boolean syncStatus
) {
    
}
