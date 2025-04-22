package com.movieticketbooking.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AppExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(
                message,
                status.value(),
                LocalDateTime.now().toString()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorResponse> handleAPIException(APIException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Handle invalid request bodies (JSON validation errors)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return buildResponse(errors.toString(), HttpStatus.BAD_REQUEST);
    }

    // Handle invalid @Min, @Pattern, etc.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        });
        return buildResponse(errors.toString(), HttpStatus.CONFLICT);
    }

    //Occurs when a request parameter or path variable cannot be converted to the required type.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                e.getValue(),
                e.getName(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "Unknown");
        return buildResponse(message, HttpStatus.BAD_REQUEST);
    }

    //Occurs when database constraints (like unique key, foreign key, or not-null constraints) are violated.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return buildResponse(e.getMostSpecificCause().getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Occurs when a query method in Spring Data JPA references a non-existing property of an entity.
    //Ex- using in case of sortBy value not matched with any property of class
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> handlePropertyReferenceException(PropertyReferenceException e) {
        return buildResponse("Invalid property name: " + e.getPropertyName(), HttpStatus.BAD_REQUEST);
    }
}
