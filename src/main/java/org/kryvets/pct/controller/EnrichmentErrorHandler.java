package org.kryvets.pct.controller;

import com.opencsv.exceptions.CsvException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class EnrichmentErrorHandler {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException exc) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("IOException occurred: " + exc.getMessage());
    }

    @ExceptionHandler(CsvException.class)
    public ResponseEntity<String> handleCsvException(CsvException exc) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("CsvException occurred: " + exc.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(CsvException exc) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred: " + exc.getMessage());
    }
}
