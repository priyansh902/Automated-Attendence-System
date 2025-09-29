package com.attendence.rural.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.DTos.HolidayDto;
import com.attendence.rural.RespDtos.Holiday_Resp;
import com.attendence.rural.Service.Holiday_service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/holidays")

@Slf4j
@Tag(name = "Holiday Management", description = "Manage school holidays")
public class Holiday_controller {
    
    private final Holiday_service holiday_service;

    public Holiday_controller(Holiday_service holiday_service){
        this.holiday_service = holiday_service;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add holiday", description = "Add a new holiday to the calendar")

    public ResponseEntity<Holiday_Resp> addHoliday(@Valid @RequestBody HolidayDto holidayDto) {

        log.info("Admin adding holiday: {}", holidayDto.date());
            Holiday_Resp response = holiday_service.addHoliday(holidayDto);
                return ResponseEntity.ok(response);

    }
    
    @PutMapping("/{holidayId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update holiday", description = "Update an existing holiday")

    public ResponseEntity<Holiday_Resp> updateHoliday(
            @PathVariable Integer holidayId,
            @Valid @RequestBody HolidayDto holidayDto) {

            log.info("Admin updating holiday with ID: {}", holidayId);
                Holiday_Resp response = holiday_service.updateHoliday(holidayId, holidayDto);
            return ResponseEntity.ok(response);

    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Get all holidays", description = "Get all active holidays")

    public ResponseEntity<List<Holiday_Resp>> getAllHolidays() {

        log.debug("Fetching all holidays");
            List<Holiday_Resp> holidays = holiday_service.getAllHolidays();
        return ResponseEntity.ok(holidays);

    }
    
    @GetMapping("/{holidayId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Get holiday by ID", description = "Get a specific holiday by ID")

    public ResponseEntity<Holiday_Resp> getHolidayById(@PathVariable Integer holidayId) {

        log.debug("Fetching holiday with ID: {}", holidayId);
            Holiday_Resp holiday = holiday_service.getHolidayById(holidayId);
        return ResponseEntity.ok(holiday);

    }
    
    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Get upcoming holidays", description = "Get upcoming holidays from today")

    public ResponseEntity<List<Holiday_Resp>> getUpcomingHolidays() {

        log.debug("Fetching upcoming holidays");
            List<Holiday_Resp> holidays = holiday_service.getUpcomingHolidays();
        return ResponseEntity.ok(holidays);

    }
    
    @GetMapping("/range")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Get holidays in range", description = "Get holidays between two dates")

    public ResponseEntity<List<Holiday_Resp>> getHolidaysInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.debug("Fetching holidays between {} and {}", startDate, endDate);
            List<Holiday_Resp> holidays = holiday_service.getHolidaysInRange(startDate, endDate);
        return ResponseEntity.ok(holidays);

    }
    
    @GetMapping("/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Get yearly holidays", description = "Get all holidays for a specific year")

    public ResponseEntity<List<Holiday_Resp>> getYearlyHolidays(@PathVariable int year) {

        log.debug("Fetching holidays for year: {}", year);
            List<Holiday_Resp> holidays = holiday_service.getYearlyHolidays(year);
        return ResponseEntity.ok(holidays);

    }
    
    @DeleteMapping("/{holidayId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete holiday", description = "Delete a holiday from calendar")

    public ResponseEntity<Void> deleteHoliday(@PathVariable Integer holidayId) {

        log.info("Admin deleting holiday with ID: {}", holidayId);
            holiday_service.deleteHoliday(holidayId);
        return ResponseEntity.noContent().build();

    }
    
    @GetMapping("/check/{date}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(summary = "Check if date is holiday", description = "Check if a specific date is holiday")

    public ResponseEntity<Boolean> isHoliday(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.debug("Checking if date is holiday: {}", date);
            boolean isHoliday = holiday_service.isHoliday(date);
        return ResponseEntity.ok(isHoliday);

    }
}
