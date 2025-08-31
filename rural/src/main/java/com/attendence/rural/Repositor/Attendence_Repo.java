package com.attendence.rural.Repositor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.attendence.rural.Model.Attendence;

@Repository
public interface Attendence_Repo extends JpaRepository<Attendence,Integer> {
    
}
