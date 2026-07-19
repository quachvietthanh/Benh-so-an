package com.benhsoan.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class TooManyLoginAttemptsException extends DomainException {

    public TooManyLoginAttemptsException() {
        super(
                HttpStatus.TOO_MANY_REQUESTS,
                "Too many login attempts."
        );
    }
}