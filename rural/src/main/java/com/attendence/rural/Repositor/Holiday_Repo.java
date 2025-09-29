package com.attendence.rural.Repositor;



import com.attendence.rural.Model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface Holiday_Repo extends JpaRepository<Holiday, Integer> {
    
    Optional<Holiday> findByDate(LocalDate date);
    
    List<Holiday> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Holiday> findByActiveTrue();
    
    List<Holiday> findByActiveTrueAndDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT h FROM Holiday h WHERE h.active = true AND h.date >= :startDate ORDER BY h.date")
    List<Holiday> findUpcomingHolidays(@Param("startDate") LocalDate startDate);
    
    boolean existsByDateAndActiveTrue(LocalDate date);
}