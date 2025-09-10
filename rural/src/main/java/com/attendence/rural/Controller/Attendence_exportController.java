package com.attendence.rural.Controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.Service.Attendence_ExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/attendence/export")
@Tag(name = "Attendance Export APIs", description = "download pdf of student attendance")
public class Attendence_exportController {

    private final Attendence_ExportService attendence_ExportService;

    public Attendence_exportController(Attendence_ExportService attendence_ExportService){
        this.attendence_ExportService = attendence_ExportService;
    }

     @Operation(summary = "Download Excel", description = "Download student attendance in Excel format")
     @GetMapping("/excel/{rollNo}")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable int rollno) throws Exception {
        var stream = attendence_ExportService.exportStudentExcel(rollno);
        byte[] bytes = stream.readAllBytes();
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_" + rollno + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @Operation(summary = "Download PDF", description = "Download student attendance in PDF format")
    @GetMapping("/pdf/{rollNo}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable int rollno) throws Exception {
        var stream = attendence_ExportService.exportStudentPdf(rollno);
        byte[] bytes = stream.readAllBytes();
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_" + rollno + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
    
}
