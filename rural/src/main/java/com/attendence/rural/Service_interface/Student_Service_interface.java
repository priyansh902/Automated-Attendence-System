package com.attendence.rural.Service_interface;

import java.util.List;

import com.attendence.rural.DTos.Student_dto;
import com.attendence.rural.RespDtos.StudentResp;

public interface Student_Service_interface {
    StudentResp createStudent(Student_dto request);
    StudentResp getStudentByRollno(int rollno);
    List<StudentResp> getAllStudents();
    void deleteStudent(int rollno);
}
