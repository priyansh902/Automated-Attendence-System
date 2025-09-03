package com.attendence.rural.Mapper;

import org.springframework.stereotype.Component;

import com.attendence.rural.DTos.Attendence_dto;
import com.attendence.rural.Model.Attendence;
import com.attendence.rural.Model.Student;
import com.attendence.rural.Repositor.Student_Repo;
import com.attendence.rural.RespDtos.Attendence_Resp;

@Component
public class Attendence_mapper {

    private Student_Repo student_Repo;

    public Attendence_mapper(Student_Repo student_Repo){
        this.student_Repo = student_Repo;
    }

    public Attendence aattendence(Attendence_dto attendence_dto){
        var attendence = new Attendence();
       
        Student student = student_Repo.
                            findByRollno(attendence_dto.rollNumber()).
                            orElseThrow(()-> new RuntimeException("Student Not found"));

        attendence.setStudent(student);
        attendence.setDate(attendence_dto.date());
        attendence.setStatus(attendence_dto.status());
        attendence.setSyncStatus(attendence_dto.syncStatus());
                            
        return attendence;
    }

    public Attendence_Resp attendence_Resp(Attendence attendence){
       
        Student s = new Attendence().getStudent();

        return new Attendence_Resp(s.getName(), s.getRollno(), s.getClassname(), s.getSchool().getName(), attendence.getDate(), attendence.getStatus(), attendence.isSyncStatus());

    }
    
}
