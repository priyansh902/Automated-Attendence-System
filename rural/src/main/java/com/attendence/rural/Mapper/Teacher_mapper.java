package com.attendence.rural.Mapper;

import org.springframework.stereotype.Service;

import com.attendence.rural.DTos.Teacher_dto;
import com.attendence.rural.Model.School;
import com.attendence.rural.Model.Teacher;
import com.attendence.rural.RespDtos.Teache_Resp;

@Service
public class Teacher_mapper {

    public Teacher ttTeacher(Teacher_dto teacher_dto){
        var teacher = new Teacher();
        teacher.setName(teacher.getName());
        teacher.setPassword(teacher.getPassword());
        teacher.setUsername(teacher_dto.username());

        var school = new School();
        school.setName(teacher_dto.schoolName());
        teacher.setSchool(school);

       return teacher;
    }

    public Teache_Resp teache_Resp(Teacher teacher){
        return new Teache_Resp(teacher.getName(), teacher.getUsername(),teacher.getSchool().getName());
    }
    
}
