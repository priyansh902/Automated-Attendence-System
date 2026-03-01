package com.attendence.rural.Excptions;

public class StudentNotFound extends RuntimeException {
      public StudentNotFound(String message) {
        super(message);
    }
}
