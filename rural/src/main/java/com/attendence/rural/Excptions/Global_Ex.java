package com.attendence.rural.Excptions;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


    @RestControllerAdvice
    public class Global_Ex {

         private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Global_Ex.class);
        
        @ExceptionHandler(StudentNotFound.class)
        public ResponseEntity<Map<String, String>> handleStudentNotFound(StudentNotFound ex) {
            logger.error("Student not found: {}", ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }

        @ExceptionHandler(SchoolNotFound.class)
        public ResponseEntity<Map<String, String>> handleSchoolNotFound(SchoolNotFound ex) {
            logger.error("School not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }

        @ExceptionHandler(Custom_ex.class)
        public ResponseEntity<Map<String, String>> handleGenericException(Custom_ex ex) {
            logger.error("Custom exception occurred: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }

        @ExceptionHandler(Exception.class) 
        public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
            logger.error("Unexpected exception occurred", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error", "details", ex.getMessage()));
        }

        @ExceptionHandler(AttendanceAlreadyExist.class)
         public ResponseEntity<Map<String,Object>> handleAttendanceExists(AttendanceAlreadyExist ex) {
            Map<String,Object> body = Map.of(
            "timestamp", Instant.now().toString(),
            "status", HttpStatus.CONFLICT.value(),
            "error", "Conflict",
            "message", ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        }

         @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<Map<String,Object>> handleSqlIntegrity(DataIntegrityViolationException ex) {
         Map<String,Object> body = Map.of(
            "timestamp", Instant.now().toString(),
            "status", HttpStatus.CONFLICT.value(),
            "error", "Conflict",
            "message", "Database constraint violation"
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        }
    }
