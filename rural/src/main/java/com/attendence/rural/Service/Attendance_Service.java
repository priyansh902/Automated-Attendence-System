package com.attendence.rural.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attendence.rural.DTos.Attendance_offlineDto;
import com.attendence.rural.DTos.Attendance_scanDto;
import com.attendence.rural.Excptions.Custom_ex;
import com.attendence.rural.Excptions.StudentNotFound;
import com.attendence.rural.Mapper.Attendance_mapper;
import com.attendence.rural.Model.Attendance;
import com.attendence.rural.Model.Status;
import com.attendence.rural.Model.Student;
import com.attendence.rural.Repositor.Attendance_Repo;
import com.attendence.rural.Repositor.Student_Repo;
import com.attendence.rural.RespDtos.Attendance_Resp;
import com.attendence.rural.RespDtos.Attendance_summaryReasp;
import com.attendence.rural.Service_interface.Attendance_interface;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class Attendance_Service implements Attendance_interface {

    private final Attendance_mapper attendence_mapper;
    private final Student_Repo student_Repo;
    private final Attendance_Repo attendence_Repo;

    public Attendance_Service(Attendance_mapper attendence_mapper,
                              Student_Repo student_Repo,
                              Attendance_Repo attendence_Repo) {
        this.attendence_Repo = attendence_Repo;
        this.attendence_mapper = attendence_mapper;
        this.student_Repo = student_Repo;
    }

     @Override
    @Transactional
    public Attendance_Resp markByScan(Attendance_scanDto dto) {
     log.info("Marking attendance via scan. Input DTO: {}", dto);

     Student student;
     if (dto.uniquecode() != null && !dto.uniquecode().isBlank()) {
        log.debug("Looking up student by QR/Unique code: {}", dto.uniquecode());
        student = student_Repo.findByUniquecode(dto.uniquecode())
                .orElseThrow(() -> {
                    log.warn("Student not found for unique code: {}", dto.uniquecode());
                    return new StudentNotFound("Student Not Found: " + dto.uniquecode());
                });
     } else if (dto.rfidTagId() != null && !dto.rfidTagId().isBlank()) {
        log.debug("Looking up student by RFID tag: {}", dto.rfidTagId());
        student = student_Repo.findByRfidTagId(dto.rfidTagId())
                .orElseThrow(() -> {
                    log.warn("Invalid RFID tag: {}", dto.rfidTagId());
                    return new StudentNotFound("Invalid RFID: " + dto.rfidTagId());
                });
      } else {
        log.error("Neither uniquecode nor rfidTagId provided in DTO");
        throw new IllegalArgumentException("Either uniquecode or rfidTagId must be provided.");
     }

     LocalDate today = LocalDate.now();

     try {
        Attendance attendence = new Attendance();
        attendence.setStudent(student);
        attendence.setDate(today);
        attendence.setStatus(Status.Present);
        attendence.setSyncStatus(false);

        Attendance saved = attendence_Repo.save(attendence);
        log.info("Attendance marked successfully for student: {}, Date: {}", student.getName(), today);
        return attendence_mapper.toResp(saved);

     } catch (DataIntegrityViolationException ex) {
        // DB constraint stopped duplicate → fetch existing and return error
        log.warn("Duplicate attendance attempt for student: {} on {}", student.getName(), today);
        Attendance existing = attendence_Repo.findByStudentAndDate(student, today)
                .orElseThrow(() -> new Custom_ex("Attendance conflict, please retry"));
        throw new Custom_ex("Attendance Already marked: " + existing.getStudent().getName());
     }
    }


    @Override
    public List<Attendance_Resp> finalizAttendenceForDate(LocalDate date) {
        log.info("Finalizing attendance for date: {}", date);

        List<Student> allsStudents = student_Repo.findAll();
        List<Attendance_Resp> absents = new ArrayList<>();

        for (Student s : allsStudents) {
            if (s == null || s.getName() == null) {
                log.warn("Skipping invalid student record: {}", s);
                continue;
            }

            boolean already = attendence_Repo.findByStudentAndDate(s, date).isPresent();
            if (!already) {
                Attendance absent = new Attendance();
                absent.setStudent(s);
                absent.setDate(date);
                absent.setStatus(Status.Absent);
                absent.setSyncStatus(false);

                Attendance saved = attendence_Repo.save(absent);

                if (saved != null && saved.getStudent() != null) {
                    Attendance_Resp resp = attendence_mapper.toResp(saved);
                    absents.add(resp);
                    log.debug("Marked absent for student: {} on {}", s.getName(), date);
                } else {
                    log.warn("Failed to save absent attendance for student: {}", s.getName());
                }
            }
        }
        log.info("Finalization complete. Total absents marked: {}", absents.size());
        return absents;
    }

    @Override
    public List<Attendance_Resp> getAttendenceByStudent(String uniquecode) {
        log.info("Fetching attendance for student: {}", uniquecode);

        Student student = student_Repo.findByUniquecode(uniquecode)
                .orElseThrow(() -> {
                    log.warn("Student not found for unique code: {}", uniquecode);
                    return new StudentNotFound("Student Not Found: " + uniquecode);
                });

        return attendence_Repo.findByStudent(student).stream()
                .map(attendence_mapper::toResp).collect(Collectors.toList());
    }

    @Override
    public Attendance_summaryReasp getAttendenceSummary(String uniquecode) {
        log.info("Fetching attendance summary for student: {}", uniquecode);

        Student student = student_Repo.findByUniquecode(uniquecode)
                .orElseThrow(() -> {
                    log.warn("Student not found for summary request: {}", uniquecode);
                    return new StudentNotFound("Student Not Found: " + uniquecode);
                });

        long total = attendence_Repo.countByStudent(student);
        long present = attendence_Repo.countByStudentAndStatus(student, Status.Present);
        double percentage = (total == 0) ? 0.0 : (present * 100.0 / total);

        log.debug("Summary for {} -> Total: {}, Present: {}, %: {}", student.getName(), total, present, percentage);

        return new Attendance_summaryReasp(
                student.getName(),
                student.getRollno(),
                student.getClassname(),
                student.getSchool() == null ? null : student.getSchool().getName(),
                total,
                present,
                percentage
        );
    }

    @Override
    public Attendance_summaryReasp getMonthlySummary(String uniquecode, int year, int month) {
        log.info("Fetching monthly summary for student: {}, Year: {}, Month: {}", uniquecode, year, month);

        Student student = student_Repo.findByUniquecode(uniquecode)
                .orElseThrow(() -> {
                    log.warn("Student not found for monthly summary: {}", uniquecode);
                    return new StudentNotFound("Student not found: " + uniquecode);
                });

        List<Attendance> monthly = attendence_Repo.findByStudent(student).stream()
                .filter(a -> a.getDate().getYear() == year && a.getDate().getMonthValue() == month)
                .collect(Collectors.toList());

        long total = monthly.size();
        long present = monthly.stream().filter(a -> a.getStatus() == Status.Present).count();
        double percentage = (total == 0) ? 0.0 : (present * 100.0 / total);

        log.debug("Monthly summary for {} -> Total: {}, Present: {}, %: {}", student.getName(), total, present, percentage);

        return new Attendance_summaryReasp(
                student.getName(),
                student.getRollno(),
                student.getClassname(),
                student.getSchool() == null ? null : student.getSchool().getName(),
                total,
                present,
                percentage
        );
    }

    @Override
    public List<Attendance_Resp> getAttendenceForDate(LocalDate date) {
        log.info("Fetching attendance for date: {}", date);

        return attendence_Repo.findByDate(date)
                .stream().map(attendence_mapper::toResp)
                .collect(Collectors.toList());
    }
     @Override
    @Transactional
    public List<Attendance_Resp> syncOfflineData(List<Attendance_offlineDto> offlineRecords) {
        log.info("Syncing {} offline attendance records", offlineRecords.size());

     List<Attendance_Resp> responses = new ArrayList<>();

        for (Attendance_offlineDto dto : offlineRecords) {
            Student student = findStudentFromDto(dto);

         try {
            // always try to insert first
            Attendance newAtt = new Attendance();
            newAtt.setStudent(student);
            newAtt.setDate(dto.date());
            newAtt.setStatus(dto.status());
            newAtt.setSyncStatus(true);

            Attendance saved = attendence_Repo.save(newAtt);
            responses.add(attendence_mapper.toResp(saved));

         } catch (DataIntegrityViolationException ex) {
            // if unique constraint fails → fetch and update existing
            Attendance existing = attendence_Repo.findFirstByStudentAndDate(student, dto.date())
                    .orElseThrow(() -> new Custom_ex("Attendance sync conflict"));
            existing.setStatus(dto.status());
            existing.setSyncStatus(true);

            Attendance saved = attendence_Repo.save(existing);
            responses.add(attendence_mapper.toResp(saved));
          }

          log.debug("Processed offline record for student: {}, Date: {}", student.getName(), dto.date());

         if (responses.size() % 50 == 0) {
            log.info("Processed {} offline attendance records so far...", responses.size());
         }
     }

     log.info("Offline sync complete. Total records processed: {}", responses.size());
     return responses;
    }


     @Override
    @Transactional
    public Attendance_Resp markByRfid(String rfidTagId) {
         log.info("RFID scan received: {}", rfidTagId);

            Student student = student_Repo.findByRfidTagId(rfidTagId)
             .orElseThrow(() -> {
                log.warn("No student found for RFID: {}", rfidTagId);
                return new StudentNotFound("No student found for RFID: " + rfidTagId);
             });

        LocalDate today = LocalDate.now();

     try {
        Attendance attendence = new Attendance();
        attendence.setStudent(student);
        attendence.setDate(today);
        attendence.setStatus(Status.Present);
        attendence.setSyncStatus(false);

        Attendance saved = attendence_Repo.save(attendence);
        log.info("Attendance marked via RFID for student: {}, Date: {}", student.getName(), today);
        return attendence_mapper.toResp(saved);

     } catch (DataIntegrityViolationException ex) {
        // DB unique constraint prevented duplicate — fetch the existing row
        log.warn("Duplicate RFID attendance attempt for student: {} on {}", student.getName(), today);
        Attendance existing = attendence_Repo.findByStudentAndDate(student, today)
                .orElseThrow(() -> new Custom_ex("Attendance conflict, please retry"));
        throw new Custom_ex("Attendance already marked: " + existing.getStudent().getName());
    }
}

    private Student findStudentFromDto(Attendance_offlineDto dto) {
        if (dto.uniquecode() != null && !dto.uniquecode().isBlank()) {
        log.debug("Lookup student by uniquecode: {}", dto.uniquecode());
        return student_Repo.findByUniquecode(dto.uniquecode())
                .orElseThrow(() -> {
                    log.warn("Invalid offline record. Student not found for code: {}", dto.uniquecode());
                    return new StudentNotFound("Invalid code: " + dto.uniquecode());
                });
     } else if (dto.rfidTagId() != null && !dto.rfidTagId().isBlank()) {
        log.debug("Lookup student by RFID tag: {}", dto.rfidTagId());
        return student_Repo.findByRfidTagId(dto.rfidTagId())
                .orElseThrow(() -> {
                    log.warn("Invalid offline record. Student not found for RFID: {}", dto.rfidTagId());
                    return new StudentNotFound("Invalid RFID: " + dto.rfidTagId());
                });
     } else {
        log.error("Offline record missing both uniquecode and RFID: {}", dto);
        throw new IllegalArgumentException("Either uniquecode or rfidTagId must be provided in offline record.");
        }
    }

}
