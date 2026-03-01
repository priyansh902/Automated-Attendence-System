package com.attendence.rural.Excptions;

public class HolidayException extends RuntimeException {
    public HolidayException(String message){
        super("Attendence can't mark on holiday");
    }
}
