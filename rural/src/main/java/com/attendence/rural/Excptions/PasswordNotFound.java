package com.attendence.rural.Excptions;

public class PasswordNotFound extends RuntimeException {
    public PasswordNotFound(String message){
        super("Password incorrect");
    }
}
