package com.attendence.rural.Excptions;

public class Invalid extends RuntimeException {
    public Invalid(String message){
        super("Invalid Token");
    }
}
