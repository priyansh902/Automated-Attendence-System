package com.attendence.rural.Excptions;

public class HolidayExist extends RuntimeException{
    public HolidayExist(String message){
        super("Holiday already exists");
    }
}
