package com.benhsoan.domain.auth.exception;

import com.benhsoan.domain.shared.exception.DomainException;

public class TooManyLoginAttemptsException extends DomainException {

    public TooManyLoginAttemptsException() {
        super("Too many login attempts. Please try again later.");
    }
}