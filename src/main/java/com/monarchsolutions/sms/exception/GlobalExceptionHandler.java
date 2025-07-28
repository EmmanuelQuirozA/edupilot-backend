package com.monarchsolutions.sms.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 400 – IllegalArgument & IllegalState
    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("type",    "error");
        body.put("title",   "Bad Request");
        body.put("message", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body);
    }

    // 400 – Constraint violations (e.g. @Validated DTOs in service methods)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.joining("; "));
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("type",    "error");
        body.put("title",   "Validation Error");
        body.put("message", msg);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body);
    }

    // 400 – @Valid / @Validated on @RequestBody or @RequestPart
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining("; "));
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("type",    "error");
        body.put("title",   "Validation Error");
        body.put("message", msg);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(body);
    }

    // 500 – catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllUncaughtException(Exception ex) {
        ex.printStackTrace();  // you can log this instead
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("type",    "error");
        body.put("title",   "Internal Server Error");
        body.put("message", "Something went wrong. Please try again.");
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(body);
    }
}
