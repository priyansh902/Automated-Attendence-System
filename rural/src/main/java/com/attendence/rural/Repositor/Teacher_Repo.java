package com.attendence.rural.Repositor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.attendence.rural.Model.School;
import com.attendence.rural.Model.Teacher;


@Repository
public interface Teacher_Repo extends JpaRepository<Teacher, Integer> {
    int countBySchool(School school);
    Optional<Teacher> findByUsername(String username);
    List<Teacher>  findBySchool(School school);
}
