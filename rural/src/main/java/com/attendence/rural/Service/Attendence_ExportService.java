package com.attendence.rural.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.attendence.rural.Model.Attendence;
import com.attendence.rural.Model.Student;
import com.attendence.rural.Repositor.Attendence_Repo;
import com.attendence.rural.Repositor.Student_Repo;
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class Attendence_ExportService {
    
    private final Attendence_Repo attendence_Repo;

    private final Student_Repo student_Repo;

    public Attendence_ExportService(Attendence_Repo attendence_Repo, Student_Repo student_Repo){
        this.attendence_Repo = attendence_Repo;
        this.student_Repo = student_Repo;
    }

    public ByteArrayInputStream exportStudentExcel(int rollNo) throws Exception {
        Student student = student_Repo.findByRollno(rollNo).orElseThrow();
        java.util.List<Attendence> records = attendence_Repo.findByStudent(student);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Attendance");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Date");
            header.createCell(1).setCellValue("Status");
            header.createCell(2).setCellValue("Synced");

            int rowIndex = 1;
            for (Attendence a : records) {
                Row r = sheet.createRow(rowIndex++);
                r.createCell(0).setCellValue(a.getDate().toString());
                r.createCell(1).setCellValue(a.getStatus().name());
                r.createCell(2).setCellValue(a.isSyncStatus() ? "Yes" : "No");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream exportStudentPdf(int rollNo) throws Exception {
        Student student = student_Repo.findByRollno(rollNo).orElseThrow();
        java.util.List<Attendence> records = attendence_Repo.findByStudent(student);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        com.itextpdf.text.Font h = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        com.itextpdf.text.Font b = FontFactory.getFont(FontFactory.HELVETICA, 12);
        document.add(new Paragraph("Attendance Report - " + student.getName(), h));
        document.add(new Paragraph("Class: " + student.getClassname() + " | Roll: " + student.getRollno(), b));
        document.add(new Paragraph("School: " + (student.getSchool()==null? "": student.getSchool().getName()), b));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell("Date");
        table.addCell("Status");
        table.addCell("Synced");

        for (Attendence a : records) {
            table.addCell(a.getDate().toString());
            table.addCell(a.getStatus().name());
            table.addCell(a.isSyncStatus() ? "Yes" : "No");
        }

        document.add(table);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
