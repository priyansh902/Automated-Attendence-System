package com.attendence.rural.Scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.attendence.rural.Service.Attendance_Service;

@Component
public class Attendance_scheduler {

    private final Attendance_Service attendence_Service;

    public Attendance_scheduler(Attendance_Service attendence_Service){
        this.attendence_Service = attendence_Service;
    }

    
    
    @Scheduled(cron = "0 5 10 * * ?")
    public void autoFinalize() {
        LocalDate today = LocalDate.now();
        attendence_Service.finalizAttendenceForDate(today);
        // you may log or persist a job-run record here
    }
    
}
