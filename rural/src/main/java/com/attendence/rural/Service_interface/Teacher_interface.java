package com.attendence.rural.Service_interface;

import java.util.List;

import com.attendence.rural.RespDtos.Teache_Resp;

public interface Teacher_interface {
    Teache_Resp getTeacherByusername(String username);
    List<Teache_Resp> getAllTeachers();
    void deleteTeacher(String username);  
}
