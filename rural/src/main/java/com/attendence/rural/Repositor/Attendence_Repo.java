package com.attendence.rural.Repositor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.attendence.rural.Model.Attendence;
import com.attendence.rural.Model.Status;
import com.attendence.rural.Model.Student;

@Repository
public interface Attendence_Repo extends JpaRepository<Attendence,Integer> {
    Optional <Attendence> findByStudentAndDate(Student student, LocalDate date);
    Optional<Attendence> findFirstByStudentAndDate(Student student, LocalDate date);

    List<Attendence> findByStudent(Student student);
    List<Attendence> findByDate(LocalDate date);

    long countByStudent(Student student);
    long countByStudentAndStatus(Student student, Status status);
    
}
