package com.attendence.rural.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attendence.rural.DTos.Student_dto;
import com.attendence.rural.Excptions.Custom_ex;
import com.attendence.rural.Excptions.SchoolNotFound;
import com.attendence.rural.Excptions.StudentNotFound;
import com.attendence.rural.Mapper.Student_mapper;
import com.attendence.rural.Model.Role;
import com.attendence.rural.Model.School;
import com.attendence.rural.Model.Student;
import com.attendence.rural.Model.User;
import com.attendence.rural.Repositor.Role_Repo;
import com.attendence.rural.Repositor.School_Repo;
import com.attendence.rural.Repositor.Student_Repo;
import com.attendence.rural.RespDtos.StudentResp;
import com.attendence.rural.Service_interface.Student_Service_interface;


import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class Student_Service implements Student_Service_interface {

    private final Student_Repo student_Repo;
    private final Student_mapper student_mapper;
    private final School_Repo school_Repo;
    private final Role_Repo role_Repo;
    private final PasswordEncoder passwordEncoder;

    public Student_Service(Student_Repo student_Repo, Student_mapper student_mapper, School_Repo school_Repo, Role_Repo role_Repo,PasswordEncoder passwordEncoder) {
        this.student_Repo = student_Repo;
        this.student_mapper = student_mapper;
        this.school_Repo = school_Repo;
        this.role_Repo = role_Repo;
        this.passwordEncoder = passwordEncoder;
    
    }

    @Override
        public StudentResp createStudent(Student_dto request) {
            log.info("Creating student with rollno={} in school={}", request.rollno(), request.schoolname());

            School school = school_Repo.findByName(request.schoolname())
             .orElseThrow(() -> {
                log.error("School not found: {}", request.schoolname());
                 return new SchoolNotFound("School not found: " + request.schoolname());
             });

         Student student;
            String uniquecode;
            do {
         uniquecode = student_mapper.generateBaseCode(school, request.rollno());
         log.debug("Generated unique code={} for rollno={}", uniquecode, request.rollno());

         if (student_Repo.findByUniquecode(uniquecode).isPresent()) {
            log.warn("Duplicate unique code={} detected, regenerating...", uniquecode);
         }

         student = student_mapper.toEntity(request, school, uniquecode);
            } while (student_Repo.findByUniquecode(uniquecode).isPresent());

             User user = new User();
             user.setUsername(request.name().toLowerCase());  // from DTO
             user.setPassword(passwordEncoder.encode(request.password())); // hash password

             Role studentRole = role_Repo.findByName("ROLE_STUDENT")
             .orElseThrow(() -> new Custom_ex("ROLE_STUDENT not found"));
                 user.setRoles(Set.of(studentRole));

                 // Link User <-> Student
                 student.setUser(user);
                 user.setStudent(student);

         Student saved = student_Repo.save(student);

         log.info("Student saved successfully → dbId={}, rollNo={}, uniqueCode={}, rfid={}",
             saved.getRollno(), saved.getUniquecode(), saved.getRfidTagId());

            return student_mapper.studentResp(saved);
    }


    @Override
    public StudentResp getStudentByRollno(int rollno) {
        log.info("Fetching student with rollno={}", rollno);

        Student student = student_Repo.findByRollno(rollno)
                .orElseThrow(() -> {
                    log.warn("Student not found with rollno={}", rollno);
                    return new StudentNotFound("Student not find with " + rollno);
                });

        log.debug("Student found: {}", student.getName());
        return student_mapper.studentResp(student);
    }

    @Override
    public List<StudentResp> getAllStudents() {
        log.info("Fetching all students...");
        List<Student> students = student_Repo.findAll();
        log.debug("Total students fetched={}", students.size());
        return student_mapper.toRespsList(students);
    }

    @Override
    public void deleteStudent(int rollno) {
        log.info("Deleting student with rollno={}", rollno);

        Student student = student_Repo.findByRollno(rollno)
                .orElseThrow(() -> {
                    log.error("Cannot delete, student not found with rollno={}", rollno);
                    return new StudentNotFound("Student not found with " + rollno);
                });

        student_Repo.delete(student);
        log.info("Deleted student with rollno={} and uniqueCode={}", rollno, student.getUniquecode());
    }

    @Override
    public List<StudentResp> createStudents(List<Student_dto> requests) {
        log.info("Bulk creating {} students", requests.size());
        List<Student> students = new ArrayList<>();

        for (Student_dto dto : requests) {
            log.debug("Processing student rollno={} for school={}", dto.rollno(), dto.schoolname());

            School school = school_Repo.findByName(dto.schoolname())
                    .orElseThrow(() -> {
                        log.error("School not found: {}", dto.schoolname());
                        return new SchoolNotFound("School not found: " + dto.schoolname());
                    });

            Student student;
            String uniquecode;
            do {
                uniquecode = student_mapper.generateBaseCode(school, dto.rollno());
                log.debug("Generated unique code={} for bulk student rollno={}", uniquecode, dto.rollno());
                student = student_mapper.toEntity(dto, school, uniquecode);
            } while (student_Repo.findByUniquecode(uniquecode).isPresent());

            students.add(student);
        }

        List<Student> savedList = student_Repo.saveAll(students);
        log.info("Bulk student save completed. Total saved={}", savedList.size());

        return student_mapper.toRespsList(savedList);
    }
}
