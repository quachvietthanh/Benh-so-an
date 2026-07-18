package com.benhsoan.domain.shared.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}