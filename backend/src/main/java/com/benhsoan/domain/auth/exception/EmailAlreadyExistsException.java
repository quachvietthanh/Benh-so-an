package com.benhsoan.domain.auth.exception;

import com.benhsoan.domain.shared.exception.DomainException;

public class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException() {
        super("Email already exists.");
    }
}