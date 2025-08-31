package com.attendence.rural.Repositor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.attendence.rural.Model.School;

@Repository
public interface School_Repo extends JpaRepository<School, Integer> {
    
}
