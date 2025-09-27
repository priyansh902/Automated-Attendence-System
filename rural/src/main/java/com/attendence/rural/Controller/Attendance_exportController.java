package com.attendence.rural.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attendence.rural.Service.Attendance_ExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/attendence/export")
@Tag(name = "Attendance Export APIs", description = "Download student attendance reports in Excel/PDF")
@Slf4j
public class Attendance_exportController {

    private final Attendance_ExportService attendence_ExportService;

    public Attendance_exportController(Attendance_ExportService attendence_ExportService){
        this.attendence_ExportService = attendence_ExportService;
    }

    @Operation(summary = "Download Excel", description = "Download student attendance in Excel format")
    @GetMapping("/excel/{rollno}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable("rollno") int rollno) throws Exception {
        log.info("Excel export requested for rollNo {}", rollno);

        try (var stream = attendence_ExportService.exportStudentExcel(rollno)) {
            byte[] bytes = stream.readAllBytes();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_" + rollno + ".xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(bytes);
        }
    }

    @Operation(summary = "Download PDF", description = "Download student attendance in PDF format")
    @GetMapping("/pdf/{rollno}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable("rollno") int rollno) throws Exception {
        log.info("PDF export requested for rollNo {}", rollno);

        try (var stream = attendence_ExportService.exportStudentPdf(rollno)) {
            byte[] bytes = stream.readAllBytes();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_" + rollno + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(bytes);
        }
    }
}
