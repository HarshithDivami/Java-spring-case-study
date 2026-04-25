package com.harshith.assigment.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base runtime exception for all domain errors.
 * Carries an {@link HttpStatus} that {@link GlobalExceptionHandler} maps directly to the HTTP response.
 */
@Getter
public class AppException extends RuntimeException {

    private final HttpStatus status;

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /** Defaults to 500 Internal Server Error when no status is specified. */
    public AppException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
