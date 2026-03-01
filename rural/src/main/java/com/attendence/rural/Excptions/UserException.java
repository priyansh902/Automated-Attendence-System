package com.attendence.rural.Excptions;

public class UserException extends RuntimeException {
    
    public UserException(String message){
        super("INvalid user || user not found");
    }
}
