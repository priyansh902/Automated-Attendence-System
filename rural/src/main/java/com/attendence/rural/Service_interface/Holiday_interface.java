package com.attendence.rural.Service_interface;

import java.time.LocalDate;
import java.util.List;

import com.attendence.rural.DTos.HolidayDto;
import com.attendence.rural.RespDtos.Holiday_Resp;

public interface Holiday_interface {
    boolean isHoliday(LocalDate date);

    Holiday_Resp addHoliday(HolidayDto holidayDto);

    List<Holiday_Resp> getAllHolidays();

    List<Holiday_Resp> getHolidaysInRange(LocalDate startDate, LocalDate endDate);

    List<Holiday_Resp> getUpcomingHolidays();

    Holiday_Resp updateHoliday(Integer holidayId, HolidayDto holidayDto);

    void deleteHoliday(Integer holidayId);

    List<Holiday_Resp> getYearlyHolidays(int year);

    Holiday_Resp getHolidayById(Integer holidayId);

     

}
