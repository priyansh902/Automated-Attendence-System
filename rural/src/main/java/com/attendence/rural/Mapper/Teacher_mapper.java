package com.attendence.rural.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.attendence.rural.DTos.Teacher_dto;
import com.attendence.rural.Model.Teacher;
import com.attendence.rural.RespDtos.Teache_Resp;

@Component
public class Teacher_mapper {

    public Teacher toEntity(Teacher_dto teacher_dto){
        var teacher = new Teacher();
        teacher.setName(teacher.getName());
        teacher.setPassword(teacher.getPassword());
        teacher.setUsername(teacher_dto.username());

        // var school = new School();
        // school.setName(teacher_dto.schoolName());O
        // teacher.setSchool(school);

       return teacher;
    }

    public Teache_Resp teache_Resp(Teacher teacher){
        return new Teache_Resp(teacher.getName(), teacher.getUsername(),teacher.getSchool().getName());
    }

    public List<Teache_Resp> toRespList(List<Teacher> teachers){
        return teachers.stream()
            .map(this::teache_Resp)
                .collect(Collectors.toList());
    }
    
}
