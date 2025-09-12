package com.attendence.rural.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.attendence.rural.DTos.Attendence_offlineDto;
import com.attendence.rural.DTos.Attendence_scanDto;
import com.attendence.rural.Excptions.Custom_ex;
import com.attendence.rural.Excptions.StudentNotFound;
import com.attendence.rural.Mapper.Attendence_mapper;
import com.attendence.rural.Model.Attendence;
import com.attendence.rural.Model.Status;
import com.attendence.rural.Model.Student;
import com.attendence.rural.Repositor.Attendence_Repo;
import com.attendence.rural.Repositor.Student_Repo;
import com.attendence.rural.RespDtos.Attendence_Resp;
import com.attendence.rural.RespDtos.Attendence_summaryReasp;
import com.attendence.rural.Service_interface.Attendence_interface;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class Attendence_Service implements Attendence_interface {

    private final Attendence_mapper attendence_mapper;
    private final Student_Repo student_Repo;
    private final Attendence_Repo attendence_Repo;

    public Attendence_Service(Attendence_mapper attendence_mapper,
                              Student_Repo student_Repo,
                              Attendence_Repo attendence_Repo) {
        this.attendence_Repo = attendence_Repo;
        this.attendence_mapper = attendence_mapper;
        this.student_Repo = student_Repo;
    }

    @Override
    public Attendence_Resp markByScan(Attendence_scanDto dto) {
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
        if (attendence_Repo.findByStudentAndDate(student, today).isPresent()) {
            log.warn("Duplicate attendance attempt for student: {} on {}", student.getName(), today);
            throw new Custom_ex("Attendance Already marked: " + student.getName());
        }

        Attendence attendence = new Attendence();
        attendence.setStudent(student);
        attendence.setDate(today);
        attendence.setStatus(Status.Present);
        attendence.setSyncStatus(false);

        Attendence saved = attendence_Repo.save(attendence);
        log.info("Attendance marked successfully for student: {}, Date: {}", student.getName(), today);
        return attendence_mapper.toResp(saved);
    }

    @Override
    public List<Attendence_Resp> finalizAttendenceForDate(LocalDate date) {
        log.info("Finalizing attendance for date: {}", date);

        List<Student> allsStudents = student_Repo.findAll();
        List<Attendence_Resp> absents = new ArrayList<>();

        for (Student s : allsStudents) {
            if (s == null || s.getName() == null) {
                log.warn("Skipping invalid student record: {}", s);
                continue;
            }

            boolean already = attendence_Repo.findByStudentAndDate(s, date).isPresent();
            if (!already) {
                Attendence absent = new Attendence();
                absent.setStudent(s);
                absent.setDate(date);
                absent.setStatus(Status.Absent);
                absent.setSyncStatus(false);

                Attendence saved = attendence_Repo.save(absent);

                if (saved != null && saved.getStudent() != null) {
                    Attendence_Resp resp = attendence_mapper.toResp(saved);
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
    public List<Attendence_Resp> getAttendenceByStudent(String uniquecode) {
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
    public Attendence_summaryReasp getAttendenceSummary(String uniquecode) {
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

        return new Attendence_summaryReasp(
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
    public Attendence_summaryReasp getMonthlySummary(String uniquecode, int year, int month) {
        log.info("Fetching monthly summary for student: {}, Year: {}, Month: {}", uniquecode, year, month);

        Student student = student_Repo.findByUniquecode(uniquecode)
                .orElseThrow(() -> {
                    log.warn("Student not found for monthly summary: {}", uniquecode);
                    return new StudentNotFound("Student not found: " + uniquecode);
                });

        List<Attendence> monthly = attendence_Repo.findByStudent(student).stream()
                .filter(a -> a.getDate().getYear() == year && a.getDate().getMonthValue() == month)
                .collect(Collectors.toList());

        long total = monthly.size();
        long present = monthly.stream().filter(a -> a.getStatus() == Status.Present).count();
        double percentage = (total == 0) ? 0.0 : (present * 100.0 / total);

        log.debug("Monthly summary for {} -> Total: {}, Present: {}, %: {}", student.getName(), total, present, percentage);

        return new Attendence_summaryReasp(
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
    public List<Attendence_Resp> getAttendenceForDate(LocalDate date) {
        log.info("Fetching attendance for date: {}", date);

        return attendence_Repo.findByDate(date)
                .stream().map(attendence_mapper::toResp)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendence_Resp> syncOfflineData(List<Attendence_offlineDto> offlineRecords) {
        log.info("Syncing {} offline attendance records", offlineRecords.size());

        List<Attendence_Resp> responses = new ArrayList<>();

      for (Attendence_offlineDto dto : offlineRecords) {
         Student student = findStudentFromDto(dto);

          Attendence attendance = attendence_Repo.findFirstByStudentAndDate(student, dto.date())
                    .orElseGet(() -> {
                     Attendence newAtt = new Attendence();
                        newAtt.setStudent(student);
                        newAtt.setDate(dto.date());
                     return newAtt;
                    });

            attendance.setStatus(dto.status());
         attendance.setSyncStatus(true);

         Attendence saved = attendence_Repo.save(attendance);
         responses.add(attendence_mapper.toResp(saved));

         log.debug("Processed offline record for student: {}, Date: {}", student.getName(), dto.date());

         if (responses.size() % 50 == 0) {
             log.info("Processed {} offline attendance records so far...", responses.size());
         }
     }

        log.info("Offline sync complete. Total records processed: {}", responses.size());
        return responses;
    }

    @Override
    public Attendence_Resp markByRfid(String rfidTagId) {
        log.info("RFID scan received: {}", rfidTagId);

        Student student = student_Repo.findByRfidTagId(rfidTagId)
                .orElseThrow(() -> {
                    log.warn("No student found for RFID: {}", rfidTagId);
                    return new StudentNotFound("No student found for RFID: " + rfidTagId);
                });

        LocalDate today = LocalDate.now();
        boolean already = attendence_Repo.findByStudentAndDate(student, today).isPresent();
        if (already) {
            log.warn("Duplicate RFID attendance attempt for student: {} on {}", student.getName(), today);
            throw new Custom_ex("Attendance already marked: " + student.getName());
        }

        Attendence attendence = new Attendence();
        attendence.setStudent(student);
        attendence.setDate(today);
        attendence.setStatus(Status.Present);
        attendence.setSyncStatus(false);

        Attendence saved = attendence_Repo.save(attendence);
        log.info("Attendance marked via RFID for student: {}, Date: {}", student.getName(), today);

        return attendence_mapper.toResp(saved);
    }


    private Student findStudentFromDto(Attendence_offlineDto dto) {
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
