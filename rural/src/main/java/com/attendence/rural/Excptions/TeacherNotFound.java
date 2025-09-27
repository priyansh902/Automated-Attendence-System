package com.attendence.rural.Excptions;


public class TeacherNotFound  extends RuntimeException{
    public TeacherNotFound(String message){
        super(message);
    }
}