package com.attendence.rural.Mapper;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;


import com.attendence.rural.DTos.HolidayDto;
import com.attendence.rural.Model.Holiday;
import com.attendence.rural.RespDtos.Holiday_Resp;

@Component
public class Holiday_mapper {
    
    
     // Convert HolidayDto to Holiday Entity
     
    public Holiday toEntity(HolidayDto holidayDto) {
        Holiday holiday = new Holiday();
        holiday.setDate(holidayDto.date());
        holiday.setDescription(holidayDto.description());
        holiday.setActive(true);
        holiday.setCreatedAt(java.time.LocalDate.now());
        return holiday;
    }
    
    
    // Convert Holiday Entity to HolidayResponse
     
    public Holiday_Resp toResponse(Holiday holiday) {
        return new Holiday_Resp(
            holiday.getId(),
            holiday.getDate(),
            holiday.getDescription(),
            holiday.isActive(),
            holiday.getCreatedAt()
        );
    }
    
    
    // Convert list of Holiday entities to HolidayResponse list
     
    public List<Holiday_Resp> toResponseList(List<Holiday> holidays) {
        return holidays.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    
     // Update existing Holiday entity with DTO data
     
    public Holiday updateEntityFromDto(Holiday existingHoliday, HolidayDto holidayDto) {
        existingHoliday.setDate(holidayDto.date());
        existingHoliday.setDescription(holidayDto.description());
        return existingHoliday;
    }
}