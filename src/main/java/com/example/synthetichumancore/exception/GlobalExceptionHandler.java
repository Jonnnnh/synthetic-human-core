package com.example.synthetichumancore.exception;

import com.example.synthetichumancore.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.RejectedExecutionException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors()
                .stream().findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse(ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", msg, req.getRequestURI());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(ValidationException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Command", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(QueueFullException.class)
    public ResponseEntity<ErrorResponse> handleQueueFull(QueueFullException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "Queue Full", ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(RejectedExecutionException.class)
    public ResponseEntity<ErrorResponse> handleRejection(RejectedExecutionException ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "Queue Full",
                "Command queue is full. Please retry later", req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", ex.getMessage(), req.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,
                                                        String error,
                                                        String message,
                                                        String path) {
        ErrorResponse body = ErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
