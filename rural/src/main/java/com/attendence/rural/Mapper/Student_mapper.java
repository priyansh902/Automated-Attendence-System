package com.attendence.rural.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.attendence.rural.DTos.Student_dto;
import com.attendence.rural.Model.Student;
import com.attendence.rural.RespDtos.StudentResp;

@Component
public class Student_mapper {
    
    public Student toEntity(Student_dto student_dto){
        var student = new Student();
        student.setName(student_dto.name());
        student.setRollno(student_dto.rollno());
        student.setClassname(student_dto.classname());
        student.setUniquecode(student_dto.uniquecode());

        // var school = new School();
        // school.setName(student_dto.schoolname());
        // student.setSchool(school);

        return student;
    }

    public List<Student> toEntryList(List<Student_dto> student_dtos){
        return student_dtos.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

    public StudentResp studentResp(Student student){
        return new StudentResp(student.getName(), student.getRollno(), student.getClassname(), student.getUniquecode(),student.getSchool().getName());
    }

    public List<StudentResp> toRespsList(List<Student> students){
        return students.stream()
            .map(this::studentResp)
            .collect(Collectors.toList());
    }

    

}
