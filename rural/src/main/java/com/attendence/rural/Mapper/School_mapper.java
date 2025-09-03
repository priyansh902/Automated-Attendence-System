package com.attendence.rural.Mapper;

import org.springframework.stereotype.Component;

import com.attendence.rural.DTos.School_dto;
import com.attendence.rural.Model.School;
import com.attendence.rural.RespDtos.School_Resp;

@Component
public class School_mapper {

    public School Sschool(School_dto school_dto){
        var school = new School();
        school.setName(school_dto.name());
        school.setLocation(school_dto.location());
        return school;
    }

    public School_Resp school_Resp(School school){
        return new School_Resp(school.getName(), school.getLocation(), school.getStudents().size(), school.getTeachers().size());
    }
    
}
