package com.attendence.rural.Service_interface;

import java.time.LocalDate;
import java.util.List;

import com.attendence.rural.DTos.Attendence_offlineDto;
import com.attendence.rural.DTos.Attendence_scanDto;
import com.attendence.rural.RespDtos.Attendence_Resp;
import com.attendence.rural.RespDtos.Attendence_summaryReasp;

public interface Attendence_interface {
    
    Attendence_Resp markByScan(Attendence_scanDto dto);

    List<Attendence_Resp> finalizAttendenceForDate(LocalDate date);

    List<Attendence_Resp> getAttendenceByStudent(String uniquecode);

    Attendence_summaryReasp getAttendenceSummary(String uniquecode);

    Attendence_summaryReasp getMonthlySummary(String uniquecode, int year, int month);

    List<Attendence_Resp> getAttendenceForDate(LocalDate date);

    List<Attendence_Resp> syncOfflineData(List<Attendence_offlineDto> offlineRecords);


}
