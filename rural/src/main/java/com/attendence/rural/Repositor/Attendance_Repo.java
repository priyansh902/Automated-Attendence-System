package com.attendence.rural.Repositor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.attendence.rural.Model.Attendance;
import com.attendence.rural.Model.Status;
import com.attendence.rural.Model.Student;

@Repository
public interface Attendance_Repo extends JpaRepository<Attendance,Integer> {
    Optional <Attendance> findByStudentAndDate(Student student, LocalDate date);
    Optional<Attendance> findFirstByStudentAndDate(Student student, LocalDate date);

    List<Attendance> findByStudent(Student student);
    List<Attendance> findByDate(LocalDate date);

    long countByStudent(Student student);
    long countByStudentAndStatus(Student student, Status status);
    
}
