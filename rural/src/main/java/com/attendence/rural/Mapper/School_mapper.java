package com.attendence.rural.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.attendence.rural.DTos.School_dto;
import com.attendence.rural.Model.School;
import com.attendence.rural.RespDtos.School_Resp;

@Component
public class School_mapper {

    private final Student_mapper student_mapper;

    private final Teacher_mapper teacher_mapper;

    public School_mapper(Student_mapper student_mapper,Teacher_mapper teacher_mapper){
        this.student_mapper = student_mapper;
       this. teacher_mapper = teacher_mapper;
    }

    public School toEntity(School_dto school_dto){
        var school = new School();
          school.setName(school_dto.name());
             school.setLocation(school_dto.location());
         return school;
    }

    public School_Resp toResp(School school){
        return new School_Resp(
                school.getName(),
                 school.getLocation(), 
                  school.getStudents().size(), 
                    school.getTeachers().size(),
                        student_mapper.toRespsList(school.getStudents()),
                        teacher_mapper.toRespList(school.getTeachers())
                        );
    }

    public List<School_Resp> toResps (List<School> school){
        return school.stream()
                .map(this::toResp)
                    .collect(Collectors.toList());
    }
    
}
