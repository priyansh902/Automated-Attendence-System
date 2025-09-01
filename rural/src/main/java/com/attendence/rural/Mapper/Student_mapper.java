package com.attendence.rural.Mapper;

import org.springframework.stereotype.Service;

import com.attendence.rural.DTos.Student_dto;
import com.attendence.rural.Model.School;
import com.attendence.rural.Model.Student;
import com.attendence.rural.RespDtos.StudentResp;

@Service
public class Student_mapper {
    
    public Student tStudent(Student_dto student_dto){
        var student = new Student();
        student.setName(student_dto.name());
        student.setRollno(student_dto.rollno());
        student.setClassname(student_dto.classname());
        student.setUniquecode(student_dto.uniquecode());

        var school = new School();
        school.setName(student_dto.Schoolname());
        student.setSchool(school);

        return student;
    }

    public StudentResp studentResp(Student student){
        return new StudentResp(student.getName(), student.getRollno(), student.getClassname(), student.getUniquecode(),student.getSchool().getName());
    }

}
