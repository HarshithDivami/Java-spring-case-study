package com.harshith.assigment.common.exception;

import org.springframework.http.HttpStatus;

public class PredictionLockedException extends AppException {

    public PredictionLockedException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
