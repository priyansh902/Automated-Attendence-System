package com.attendence.rural.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attendence.rural.DTos.School_dto;
import com.attendence.rural.Excptions.Custom_ex;
import com.attendence.rural.Excptions.SchoolNotFound;
import com.attendence.rural.Mapper.School_mapper;
import com.attendence.rural.Model.School;
import com.attendence.rural.Repositor.School_Repo;
import com.attendence.rural.RespDtos.School_Resp;
import com.attendence.rural.Service_interface.School_interface;

@Service
@Transactional
public class School_Service implements School_interface {

    private final School_Repo school_Repo;

    private final School_mapper school_mapper;

    public School_Service(School_Repo school_Repo,School_mapper school_mapper){
        this.school_Repo= school_Repo;
        this.school_mapper= school_mapper;
    }

    @Override
    public School_Resp createSchool(School_dto request) {
       
        if(school_Repo.findByName(request.name()).isPresent()) {
            throw new Custom_ex("school already exists " + request.name());

        }

        School school = school_mapper.toEntity(request);
        School saved = school_Repo.save(school);

        return school_mapper.toResp(saved);

    }

    @Override
    public School_Resp getSchoolByName(String name) {

       School school = school_Repo.findByName(name)
                        .orElseThrow(() -> new SchoolNotFound("school Not Found " + name));

            return school_mapper.toResp(school);

    }

    @Override
    public List<School_Resp> getAllSchools() {
      
        return school_mapper.toResps(school_Repo.findAll());
  
    }

    @Override
    public void deleteSchool(String name) {
       
        School school = school_Repo.findByName(name)
                        .orElseThrow(() -> new SchoolNotFound("School Not Exist " + name));

            school_Repo.delete(school);
    }



    
}
