package com.attendence.rural.Service_interface;

import java.time.LocalDate;
import java.util.List;

import com.attendence.rural.DTos.Attendance_offlineDto;
import com.attendence.rural.DTos.Attendance_scanDto;
import com.attendence.rural.RespDtos.Attendance_Resp;
import com.attendence.rural.RespDtos.Attendance_summaryReasp;

public interface Attendance_interface {
    
    Attendance_Resp markByScan(Attendance_scanDto dto);

    List<Attendance_Resp> finalizAttendenceForDate(LocalDate date);

    List<Attendance_Resp> getAttendenceByStudent(String uniquecode);

    Attendance_summaryReasp getAttendenceSummary(String uniquecode);

    Attendance_summaryReasp getMonthlySummary(String uniquecode, int year, int month);

    List<Attendance_Resp> getAttendenceForDate(LocalDate date);

    public List<Attendance_Resp> syncOfflineData(List<Attendance_offlineDto> offlineRecords);

    Attendance_Resp markByRfid(String rfidTagId);



}
