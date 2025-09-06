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

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/attendence")
public class Attendence_Controller {
    
    private final Attendence_Service attendence_Service;

    public Attendence_Controller(Attendence_Service attendence_Service){
        this.attendence_Service = attendence_Service;
    }

     // Student scans in the database
    @PostMapping("/scan")
    public ResponseEntity<Attendence_Resp> scan(@Valid @RequestBody Attendence_scanDto dto) {
        return ResponseEntity.ok(attendence_Service.markByScan(dto));
    }

    // Teacher triggers finalize (optional; scheduler will also run)
    @PostMapping("/finalize")
    public ResponseEntity<List<Attendence_Resp>> finalizeToday() {
        List<Attendence_Resp> marked = attendence_Service.finalizAttendenceForDate(LocalDate.now());
        return ResponseEntity.ok(marked);
    }

    // Student records in to the database
    @GetMapping("/student/{uniqueCode}")
    public ResponseEntity<List<Attendence_Resp>> getStudentRecords(@PathVariable String uniqueCode) {
        return ResponseEntity.ok(attendence_Service.getAttendenceByStudent(uniqueCode));
    }

    // Summary of the student in the database
    @GetMapping("/student/{uniqueCode}/summary")
    public ResponseEntity<Attendence_summaryReasp> getSummary(@PathVariable String uniqueCode) {
        return ResponseEntity.ok(attendence_Service.getAttendenceSummary(uniqueCode));
    }

    // Monthly summary int he database
    @GetMapping("/student/{uniqueCode}/summary/{year}/{month}")
    public ResponseEntity<Attendence_summaryReasp> getMonthly(
            @PathVariable String uniqueCode, @PathVariable int year, @PathVariable int month) {
        return ResponseEntity.ok(attendence_Service.getMonthlySummary(uniqueCode, year, month));
    }

    @PostMapping("/sync")
    public ResponseEntity<List<Attendence_Resp>> syncOffline(
        @Valid @RequestBody List<Attendence_offlineDto> offlineRecords) {
            return ResponseEntity.ok(attendence_Service.syncOfflineData(offlineRecords));
}

}
