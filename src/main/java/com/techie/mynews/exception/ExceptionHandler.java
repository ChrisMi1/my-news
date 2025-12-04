package com.techie.mynews.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(MyNewsException.class)
    public ResponseEntity<String> handlePlnException(HttpServletRequest request, MyNewsException myNewsException) {
        return ResponseEntity.status(myNewsException.getStatusCode()).body(myNewsException.getMessage());
    }
}
