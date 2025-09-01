package com.attendence.rural.Repositor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.attendence.rural.Model.School;
import com.attendence.rural.Model.Student;


@Repository
public interface Student_Repo extends JpaRepository<Student, Integer> {
    int countBySchool(School school);

    Optional  <Student> findByRollno(int rollno);
    Optional <Student> findByUniquecode(String uniquecode);
    
}
