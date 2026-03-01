package com.attendence.rural.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attendence.rural.Excptions.StudentNotFound;
import com.attendence.rural.Model.Attendance;
import com.attendence.rural.Model.Student;
import com.attendence.rural.Repositor.Attendance_Repo;
import com.attendence.rural.Repositor.Student_Repo;
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class Attendance_ExportService {
    
    private final Attendance_Repo attendence_Repo;
    private final Student_Repo student_Repo;

    public Attendance_ExportService(Attendance_Repo attendence_Repo, Student_Repo student_Repo) {
        this.attendence_Repo = attendence_Repo;
        this.student_Repo = student_Repo;
    }

    public ByteArrayInputStream exportStudentExcel(int rollNo) throws Exception {
        log.info("Exporting attendance to Excel for rollNo: {}", rollNo);

        Student student = student_Repo.findByRollno(rollNo)
                .orElseThrow(() -> {
                    log.error("No student found with rollNo: {}", rollNo);
                    return new StudentNotFound("Student not found: " + rollNo);
                });

        java.util.List<Attendance> records = attendence_Repo.findByStudent(student);
        log.debug("Fetched {} attendance records for student: {}", records.size(), student.getName());

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Attendance");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Date");
            header.createCell(1).setCellValue("Status");
            header.createCell(2).setCellValue("RFID");
            header.createCell(3).setCellValue("Synced");

            int rowIndex = 1;
            for (Attendance a : records) {
                Row r = sheet.createRow(rowIndex++);
                r.createCell(0).setCellValue(a.getDate().toString());
                r.createCell(1).setCellValue(a.getStatus().name());
                r.createCell(2).setCellValue(student.getRfidTagId());
                r.createCell(3).setCellValue(a.isSyncStatus() ? "Yes" : "No");
            }

            workbook.write(out);
            log.info("Excel export successful for student: {} (rows: {})", student.getName(), records.size());
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            log.error("Error exporting Excel for rollNo: {}", rollNo, e);
            throw e;
        }
    }

    public ByteArrayInputStream exportStudentPdf(int rollNo) throws Exception {
        log.info("Exporting attendance to PDF for rollNo: {}", rollNo);

        Student student = student_Repo.findByRollno(rollNo)
                .orElseThrow(() -> {
                    log.error("No student found with rollNo: {}", rollNo);
                    return new StudentNotFound("Student not found: " + rollNo);
                });

        java.util.List<Attendance> records = attendence_Repo.findByStudent(student);
        log.debug("Fetched {} attendance records for student: {}", records.size(), student.getName());

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            com.itextpdf.text.Font h = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            com.itextpdf.text.Font b = FontFactory.getFont(FontFactory.HELVETICA, 12);
            document.add(new Paragraph("Attendance Report - " + student.getName(), h));
            document.add(new Paragraph("Class: " + student.getClassname() + " | Roll: " + student.getRollno(), b));
            document.add(new Paragraph("School: " + (student.getSchool() == null ? "" : student.getSchool().getName()), b));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Date");
            table.addCell("RFID");
            table.addCell("Status");
            table.addCell("Synced");

            for (Attendance a : records) {
                table.addCell(a.getDate().toString());
                table.addCell(student.getRfidTagId() != null ? student.getRfidTagId() : "N/A");
                table.addCell(a.getStatus().name());
                table.addCell(a.isSyncStatus() ? "Yes" : "No");
            }

            document.add(table);
            log.info("PDF export successful for student: {} (rows: {})", student.getName(), records.size());
        } catch (Exception e) {
            log.error("Error exporting PDF for rollNo: {}", rollNo, e);
            throw e;
        } finally {
            document.close();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
