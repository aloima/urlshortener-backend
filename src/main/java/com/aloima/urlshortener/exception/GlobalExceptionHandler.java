package com.aloima.urlshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<HTTPErrorResponse> handleNotFound(ResourceNotFoundException exception, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        HTTPErrorResponse error = new HTTPErrorResponse(exception.getMessage(), uri, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<HTTPErrorResponse> handleInvalidFormat(InvalidFormatException exception, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        HTTPErrorResponse error = new HTTPErrorResponse(exception.getMessage(), uri, exception.data);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
