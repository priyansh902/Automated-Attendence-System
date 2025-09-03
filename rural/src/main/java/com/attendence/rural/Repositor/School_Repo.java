package com.attendence.rural.Repositor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.attendence.rural.Model.School;



@Repository
public interface School_Repo extends JpaRepository<School, Integer> {
    Optional <School> findByName(String name);

    Optional <School> findByLocation(String location);
    
}
