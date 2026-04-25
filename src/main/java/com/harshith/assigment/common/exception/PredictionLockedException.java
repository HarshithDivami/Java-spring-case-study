package com.harshith.assigment.common.exception;

import org.springframework.http.HttpStatus;

/** Thrown when a prediction is submitted after the lock deadline; maps to HTTP 422. */
public class PredictionLockedException extends AppException {

    public PredictionLockedException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
