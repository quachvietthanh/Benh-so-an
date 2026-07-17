package com.benhsoan.domain.auth.exception;

import com.benhsoan.domain.shared.exception.DomainException;

public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException() {
        super("Invalid username or password.");
    }
}