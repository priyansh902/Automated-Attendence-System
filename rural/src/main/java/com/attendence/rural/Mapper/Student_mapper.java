package com.attendence.rural.Mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.attendence.rural.DTos.Student_dto;
import com.attendence.rural.Model.School;
import com.attendence.rural.Model.Student;
import com.attendence.rural.Repositor.Student_Repo;
import com.attendence.rural.RespDtos.StudentResp;

@Component
public class Student_mapper {

    private final Student_Repo student_Repo;

    Student_mapper(Student_Repo student_Repo) {
        this.student_Repo = student_Repo;
    }
    
    public Student toEntity(Student_dto student_dto){
        var student = new Student();
        student.setName(student_dto.name());
        student.setRollno(student_dto.rollno());
        student.setClassname(student_dto.classname());

         student.setRfidTagId(generateRandomRFID());
        

        //   if (student_dto.rfidTagId() == null || student_dto.rfidTagId().isBlank()) {
        //     student.setRfidTagId(generateRandomRFID());
        //     } else {
        //     student.setRfidTagId(student_dto.rfidTagId());
        //     }

        // student.setUniquecode(student_dto.uniquecode());

        // student.getUniquecode();

        // var school = new School();
        // school.setName(student_dto.schoolname());
        // student.setSchool(school);

        return student;
    }

    public Student toEntity(Student_dto dto, School school, String uniqueCode) {
        Student student = new Student();
         student.setName(dto.name());
             student.setRollno(dto.rollno());
                 student.setClassname(dto.classname());
                     student.setUniquecode(uniqueCode);
                        
                         student.setSchool(school);
                          student.setRfidTagId(generateRandomRFID());


                        //  if (dto.rfidTagId() == null || dto.rfidTagId().isBlank()) {
                        //  student.setRfidTagId(generateRandomRFID());
                        //     } else {
                        // student.setRfidTagId(dto.rfidTagId());
                        //   }
         return student;
    }

    public String generateBaseCode (School school, int rollno){
        String schoolCode = school.getName()
                            .replaceAll("\\s+","")
                            .substring(0,3)
                            .toUpperCase();
        String random = UUID.randomUUID().toString().substring(0,3).toUpperCase();

        return schoolCode + "-"+rollno+"-"+random;
    }


     public List<Student> toEntryList(List<Student_dto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    
    // public List<Student> toEntryList(List<Student_dto> student_dtos){
    //     return student_dtos.stream()
    //      .map(dto -> {
    //         Student student = toEntity(dto); 
            
    //         if (student.getRfidTagId() == null || student.getRfidTagId().isBlank()) {
    //             student.setRfidTagId(generateRandomRFID());
    //         }
    //         return student;
    //     })
    //     .collect(Collectors.toList());
    // }

     public String generateRandomRFID() {
        String rfid;
        do {
            rfid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
        } while (student_Repo.findByRfidTagId(rfid).isPresent());
        return rfid;
        }

    public StudentResp studentResp(Student student){
        return new StudentResp(student.getName(), 
                                student.getRollno(),
                                 student.getClassname(), 
                                    student.getUniquecode(),
                                        student.getRfidTagId(),
                                         student.getSchool() == null ? null :student.getSchool().getName());
    }

    

    public List<StudentResp> toRespsList(List<Student> students){
        return students.stream()
            .map(this::studentResp)
            .collect(Collectors.toList());
    }

    

}
