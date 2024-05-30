package com.carrental.exception;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<String> mappedErrors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();

        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                mappedErrors);

        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                List.of(ex.getMessage()));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationException(RegistrationException ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                List.of(ex.getMessage()));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private String getErrorMessage(ObjectError objectError) {
        if (objectError instanceof FieldError fieldError) {
            String field = fieldError.getField();
            String defaultMessage = objectError.getDefaultMessage();

            return field + " " + defaultMessage;
        }

        return objectError.getDefaultMessage();
    }
}
