package com.attendence.rural.Excptions;

public class AttendanceAlreadyExist extends RuntimeException {
    
    public AttendanceAlreadyExist(String message){
        super(message);
    }

}
