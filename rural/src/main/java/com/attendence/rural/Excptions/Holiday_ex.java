package com.attendence.rural.Excptions;

public class Holiday_ex extends RuntimeException{
    public Holiday_ex(String message){
        super("Holiday Not found");
    }
}
