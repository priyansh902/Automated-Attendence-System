package com.attendence.rural.Service_interface;

import java.util.List;

import com.attendence.rural.DTos.School_dto;
import com.attendence.rural.RespDtos.School_Resp;

public interface School_interface {

    School_Resp createSchool(School_dto request);
    School_Resp getSchoolByName(String name);
    List<School_Resp> getAllSchools();
    void deleteSchool(String name);
    
}
