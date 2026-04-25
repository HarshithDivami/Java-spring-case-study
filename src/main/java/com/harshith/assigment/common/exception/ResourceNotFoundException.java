package com.harshith.assigment.common.exception;

import org.springframework.http.HttpStatus;

/** Thrown when a requested entity does not exist; maps to HTTP 404. */
public class ResourceNotFoundException extends AppException {

    /**
     * Produces a standardised message: {@code "<resourceName> not found with <fieldName>: '<fieldValue>'"}.
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
