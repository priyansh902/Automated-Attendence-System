package com.attendence.rural.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.DTos.Attendence_offlineDto;
import com.attendence.rural.DTos.Attendence_scanDto;
import com.attendence.rural.RespDtos.Attendence_Resp;
import com.attendence.rural.RespDtos.Attendence_summaryReasp;
import com.attendence.rural.Service.Attendence_Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/attendence")
@Tag(name = "Attendance APIs", description = "Manage student attendance")
public class Attendence_Controller {
    
    private final Attendence_Service attendence_Service;

    public Attendence_Controller(Attendence_Service attendence_Service){
        this.attendence_Service = attendence_Service;
    }

    @Operation(summary = "Mark attendance by scan", description = "Student scans their unique code to mark attendance")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Attendance marked successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
     // Student scans in the database
    @PostMapping("/scan")
    public ResponseEntity<Attendence_Resp> scan(@Valid @RequestBody Attendence_scanDto dto) {
        return ResponseEntity.ok(attendence_Service.markByScan(dto));
    }


   
     @Operation(summary = "Finalize today's attendance", description = "Teacher finalizes attendance manually. Also runs automatically by scheduler.")
   
    @PostMapping("/finalize")
    public ResponseEntity<List<Attendence_Resp>> finalizeToday() {
        List<Attendence_Resp> marked = attendence_Service.finalizAttendenceForDate(LocalDate.now());
        return ResponseEntity.ok(marked);
    }

    
    @Operation(summary = "Get student attendance records", description = "Retrieve all attendance records of a student")
    @GetMapping("/student/{uniqueCode}")
    public ResponseEntity<List<Attendence_Resp>> getStudentRecords(@PathVariable String uniqueCode) {
        return ResponseEntity.ok(attendence_Service.getAttendenceByStudent(uniqueCode));
    }

    @Operation(summary = "Get attendance summary", description = "Retrieve total days, present days, and percentage for a student")
    @GetMapping("/student/{uniqueCode}/summary")
    public ResponseEntity<Attendence_summaryReasp> getSummary(@PathVariable String uniqueCode) {
        return ResponseEntity.ok(attendence_Service.getAttendenceSummary(uniqueCode));
    }

    
     @Operation(summary = "Get monthly summary", description = "Retrieve attendance summary of a student for a given month")
    @GetMapping("/student/{uniqueCode}/summary/{year}/{month}")
    public ResponseEntity<Attendence_summaryReasp> getMonthly(
            @PathVariable String uniqueCode, @PathVariable int year, @PathVariable int month) {
        return ResponseEntity.ok(attendence_Service.getMonthlySummary(uniqueCode, year, month));
    }

    @Operation(summary = "Sync offline data", description = "Upload and sync attendance data when back online")
    @PostMapping("/sync")
    public ResponseEntity<List<Attendence_Resp>> syncOffline(
        @Valid @RequestBody List<Attendence_offlineDto> offlineRecords) {
            return ResponseEntity.ok(attendence_Service.syncOfflineData(offlineRecords));
}

}
