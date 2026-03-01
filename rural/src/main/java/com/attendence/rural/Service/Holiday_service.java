package com.attendence.rural.Service;



import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attendence.rural.DTos.HolidayDto;
import com.attendence.rural.Excptions.HolidayExist;
import com.attendence.rural.Excptions.Holiday_ex;
import com.attendence.rural.Mapper.Holiday_mapper;
import com.attendence.rural.Model.Holiday;
import com.attendence.rural.Repositor.Holiday_Repo;
import com.attendence.rural.RespDtos.Holiday_Resp;
import com.attendence.rural.Service_interface.Holiday_interface;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j

public class Holiday_service implements Holiday_interface {
    
    private final Holiday_Repo holidayRepo;
    private final Holiday_mapper holidayMapper; 

    public Holiday_service(Holiday_Repo holiday_Repo, Holiday_mapper holiday_mapper){
        this.holidayMapper = holiday_mapper;
        this.holidayRepo = holiday_Repo;
    }
    
    
     // Check if a date is a holiday
     @Override
    public boolean isHoliday(LocalDate date) {
        boolean isHoliday = holidayRepo.existsByDateAndActiveTrue(date);
        log.debug("Date {} is holiday: {}", date, isHoliday);
        return isHoliday;
    }
    
    
    //  Add a new holiday
     @Override
    public Holiday_Resp addHoliday(HolidayDto holidayDto) {
        log.info("Adding holiday: {} - {}", holidayDto.date(), holidayDto.description());
        
        // Check if holiday already exists
        if (holidayRepo.findByDate(holidayDto.date()).isPresent()) {
            log.warn("Holiday already exists for date: {}", holidayDto.date());
            throw new HolidayExist("Holiday already exists for date: " + holidayDto.date());
        }
        
        
        Holiday holiday = holidayMapper.toEntity(holidayDto);
        Holiday saved = holidayRepo.save(holiday);
        
        log.info("Holiday added successfully: {}", saved.getDescription());
        
        
        return holidayMapper.toResponse(saved);
    }
    
    
    //  Get all active holidays
     @Override
    public List<Holiday_Resp> getAllHolidays() {
        log.debug("Fetching all active holidays");
        List<Holiday> holidays = holidayRepo.findByActiveTrue();
        
        
        return holidayMapper.toResponseList(holidays);
    }
    
    
    //  Get holidays for a date range
     @Override
    public List<Holiday_Resp> getHolidaysInRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching holidays between {} and {}", startDate, endDate);
        List<Holiday> holidays = holidayRepo.findByActiveTrueAndDateBetween(startDate, endDate);
        
        // Use mapper
        return holidayMapper.toResponseList(holidays);
    }
    
    
    //  Get upcoming holidays
     @Override
    public List<Holiday_Resp> getUpcomingHolidays() {
        log.debug("Fetching upcoming holidays");
        List<Holiday> holidays = holidayRepo.findUpcomingHolidays(LocalDate.now());
        
        
        return holidayMapper.toResponseList(holidays);
    }
    
    
   //  Update an existing holiday
     @Override
    public Holiday_Resp updateHoliday(Integer holidayId, HolidayDto holidayDto) {
        log.info("Updating holiday with ID: {}", holidayId);
        
        Holiday existingHoliday = holidayRepo.findById(holidayId)
                .orElseThrow(() -> {
                    log.warn("Holiday not found with ID: {}", holidayId);
                    return new Holiday_ex("Holiday not found");
                });
        
        
        Holiday updatedHoliday = holidayMapper.updateEntityFromDto(existingHoliday, holidayDto);
        Holiday saved = holidayRepo.save(updatedHoliday);
        
        log.info("Holiday updated successfully: {}", saved.getDescription());
        
    
        return holidayMapper.toResponse(saved);
    }
    
    
    //  Delete a holiday (soft delete - set active=false)
     @Override
    public void deleteHoliday(Integer holidayId) {
        log.info("Deleting holiday with ID: {}", holidayId);
        
        Holiday holiday = holidayRepo.findById(holidayId)
                .orElseThrow(() -> {
                    log.warn("Holiday not found with ID: {}", holidayId);
                    return new Holiday_ex("Holiday not found");
                });
        
        holiday.setActive(false);
        holidayRepo.save(holiday);
        
        log.info("Holiday deleted successfully: {}", holiday.getDescription());
    }
    
    
    //  Get holiday calendar for a year
     @Override
    public List<Holiday_Resp> getYearlyHolidays(int year) {
        log.debug("Fetching holidays for year: {}", year);
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        List<Holiday> holidays = holidayRepo.findByActiveTrueAndDateBetween(startDate, endDate);
        
        
        return holidayMapper.toResponseList(holidays);
    }
    
    
    // Get holiday by ID
     @Override
    public Holiday_Resp getHolidayById(Integer holidayId) {
        log.debug("Fetching holiday with ID: {}", holidayId);
        
        Holiday holiday = holidayRepo.findById(holidayId)
                .orElseThrow(() -> {
                    log.warn("Holiday not found with ID: {}", holidayId);
                    return new Holiday_ex("Holiday not found");
                });
        
        
        return holidayMapper.toResponse(holiday);
    }
}
