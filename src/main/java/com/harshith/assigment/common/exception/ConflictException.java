package com.harshith.assigment.common.exception;

import org.springframework.http.HttpStatus;

/** Thrown when a create/update would violate a uniqueness constraint; maps to HTTP 409. */
public class ConflictException extends AppException {

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
