package com.attendence.rural.RespDtos;

import java.util.List;

public record School_Resp(
    String name,
    String location,
    int totalStudents,
    int totalteachers,
    List<StudentResp> students,
    List<Teache_Resp> teachers
) {
    
}
