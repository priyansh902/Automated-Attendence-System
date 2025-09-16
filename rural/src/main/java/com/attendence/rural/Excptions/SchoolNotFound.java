package com.attendence.rural.Excptions;

public class SchoolNotFound extends RuntimeException{
     public SchoolNotFound(String message) {
        super(message);
    }
}
