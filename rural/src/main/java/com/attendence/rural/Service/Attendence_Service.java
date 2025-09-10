package com.attendence.rural.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

@Service
@Transactional
public class Attendence_Service implements Attendence_interface {

    private final Attendence_mapper attendence_mapper;
    private final Student_Repo student_Repo;
    private final Attendence_Repo attendence_Repo;

    public Attendence_Service ( Attendence_mapper attendence_mapper, Student_Repo student_Repo, Attendence_Repo attendence_Repo){
        this.attendence_Repo =attendence_Repo;
        this.attendence_mapper = attendence_mapper;
        this.student_Repo = student_Repo;
    }

    @Override
    public Attendence_Resp markByScan(Attendence_scanDto dto) {
    
        Student student = student_Repo.findByUniquecode(dto.uniquecode())
                            .orElseThrow(() -> new StudentNotFound("Student Not Found: " + dto.uniquecode()));

        LocalDate today = LocalDate.now();
        attendence_Repo.findByStudentAndDate(student, today)
                                                .orElseThrow(() -> new Custom_ex("Attendence Already marked: " + student.getName()));

        Attendence attendence = new Attendence();
        attendence.setStudent(student);
        attendence.setDate(today);
        attendence.setStatus(Status.Present);
        attendence.setSyncStatus(false);

        Attendence saved = attendence_Repo.save(attendence);
        return attendence_mapper.toResp(saved);
    }

    @Override
    public List<Attendence_Resp> finalizAttendenceForDate(LocalDate date) {
    
        List<Student> allsStudents = student_Repo.findAll();
        List<Attendence_Resp> absents = new ArrayList<>();

        
        for(Student s : allsStudents){

            if(s == null  || s.getName() == null){
                System.out.println("Skip invalid student: "+ s);
                continue;
            } 
            
            boolean Already = attendence_Repo.findByStudentAndDate(s, date).isPresent();
            if(!Already) {
                Attendence absent = new Attendence();

                absent.setStudent(s);
                absent.setDate(date);
                absent.setStatus(Status.Absent);
                absent.setSyncStatus(false);
                Attendence saved = attendence_Repo.save(absent);
                // absents.add(attendence_mapper.toResp(saved));

                  if (saved != null && saved.getStudent() != null) {
                      Attendence_Resp resp = attendence_mapper.toResp(saved);
                         
                             absents.add(resp);
                 } else {
             System.out.println("Warning: Saved attendance or student is null for student: " + s.getName());
             }
        }
    }
             
        return absents;
    }

    @Override
    public List<Attendence_Resp> getAttendenceByStudent(String uniquecode) {
      
        Student student = student_Repo.findByUniquecode(uniquecode)
                            .orElseThrow(() -> new StudentNotFound("Student Not Found: "+uniquecode));

        return attendence_Repo.findByStudent(student).stream()
                                .map(attendence_mapper::toResp).collect(Collectors.toList());
    }

    @Override
    public Attendence_summaryReasp getAttendenceSummary(String uniquecode) {
     
        Student student = student_Repo.findByUniquecode(uniquecode)
                            .orElseThrow(() -> new StudentNotFound("Student Not Found: "+uniquecode));

            long total = attendence_Repo.countByStudent(student);
            long present = attendence_Repo.countByStudentAndStatus(student, Status.Present);
            double percentage = (total == 0) ? 0.0 :(present * 100 / total);

       
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
        
         Student student = student_Repo.findByUniquecode(uniquecode)
                          .orElseThrow(() -> new StudentNotFound("Student not found: " + uniquecode));

        List<Attendence> monthly = attendence_Repo.findByStudent(student).stream()
                .filter(a -> a.getDate().getYear() == year && a.getDate().getMonthValue() == month)
                .collect(Collectors.toList());

        long total = monthly.size();
        long present = monthly.stream().filter(a -> a.getStatus() == Status.Present).count();
        double percentage = (total == 0) ? 0.0 : (present * 100.0 / total);

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
       
        return
            attendence_Repo.findByDate(date)
                    .stream().map(attendence_mapper::toResp)
                        .collect(Collectors.toList());
        
    }

    @Override
    public List<Attendence_Resp> syncOfflineData(List<Attendence_offlineDto> offlineRecords) {
    
        List<Attendence_Resp> responses = new ArrayList<>();

            for (Attendence_offlineDto dto : offlineRecords) {
             Student student = student_Repo.findByUniquecode(dto.uniquecode())
                .orElseThrow(() -> new StudentNotFound("Invalid code: " + dto.uniquecode()));

             // check if record already exists
                 Optional<Attendence> existing = attendence_Repo.findFirstByStudentAndDate(student, dto.date());
             if (existing.isPresent()) {
                Attendence att = existing.get();
                 att.setStatus(dto.status());
                     att.setSyncStatus(true);
                         Attendence updated = attendence_Repo.save(att);
             responses.add(attendence_mapper.toResp(updated)); 
             continue;
             }

            Attendence attendance = new Attendence();
             attendance.setStudent(student);
                 attendance.setDate(dto.date());
                     attendance.setStatus(dto.status());
                        attendance.setSyncStatus(true); // ✅ flagged as offline sync

        Attendence saved = attendence_Repo.save(attendance);
        responses.add(attendence_mapper.toResp(saved));
            }
         return responses;
        
    }

   
    
}
