package com.attendence.rural.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attendence.rural.DTos.Student_dto;
import com.attendence.rural.Excptions.SchoolNotFound;
import com.attendence.rural.Excptions.StudentNotFound;
import com.attendence.rural.Mapper.Student_mapper;
import com.attendence.rural.Model.School;
import com.attendence.rural.Model.Student;
import com.attendence.rural.Repositor.School_Repo;
import com.attendence.rural.Repositor.Student_Repo;
import com.attendence.rural.RespDtos.StudentResp;
import com.attendence.rural.Service_interface.Student_Service_interface;



@Service
@Transactional
public class Student_Service implements Student_Service_interface {

    private final Student_Repo student_Repo;

    private final Student_mapper student_mapper;

    private final School_Repo school_Repo;

    public Student_Service(Student_Repo student_Repo,Student_mapper student_mapper, School_Repo school_Repo){
       this. student_Repo = student_Repo;
       this. student_mapper = student_mapper;
       this. school_Repo = school_Repo;
    }

      @Override
    public StudentResp createStudent(Student_dto request) {
        School school = school_Repo.findByName(request.schoolname())
                .orElseThrow(() -> new SchoolNotFound("School not found: " + request.schoolname()));

        Student student;
        String uniquecode;
        do {
            uniquecode = student_mapper.generateBaseCode(school, request.rollno());
            student = student_mapper.toEntity(request, school, uniquecode);
        } while (student_Repo.findByUniquecode(uniquecode).isPresent());

        Student saved = student_Repo.save(student);
        return student_mapper.studentResp(saved);
    }

    @Override
    public StudentResp getStudentByRollno(int rollno) {
        Student student = student_Repo.findByRollno(rollno)
            .orElseThrow(() -> new StudentNotFound("Student not find with " + rollno));

            return student_mapper.studentResp(student);
    }

    @Override
    public List<StudentResp> getAllStudents() {
        return student_mapper.toRespsList(student_Repo.findAll());
         }

    @Override
    public void deleteStudent(int rollno) {
        Student student = student_Repo.findByRollno(rollno)
            .orElseThrow(() -> new StudentNotFound("Student not found with " + rollno));
            student_Repo.delete(student);

    }

    // Bulk registration
    @Override
    public List<StudentResp> createStudents(List<Student_dto> requests) {
        List<Student> students = new ArrayList<>();

        for (Student_dto dto : requests) {
            School school = school_Repo.findByName(dto.schoolname())
                    .orElseThrow(() -> new SchoolNotFound("School not found: " + dto.schoolname()));

            Student student;
            String uniquecode;
            do {
                uniquecode = student_mapper.generateBaseCode(school, dto.rollno());
                student = student_mapper.toEntity(dto, school, uniquecode);
            } while (student_Repo.findByUniquecode(uniquecode).isPresent());

            students.add(student);
        }

        List<Student> savedList = student_Repo.saveAll(students);
        return student_mapper.toRespsList(savedList);
    }

     

     
    
    

    
}
