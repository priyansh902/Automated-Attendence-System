package com.attendence.rural.RespDtos;

public record Attendence_summaryReasp(
    String studentName,
    int rollNumber,
    String className,
    String schoolName,
    long totalDays,
    long presentDays,
    double percentage
) {
    
}
