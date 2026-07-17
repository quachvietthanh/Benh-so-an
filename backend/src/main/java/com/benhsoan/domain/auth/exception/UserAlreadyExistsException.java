package com.benhsoan.domain.auth.exception;

import com.benhsoan.domain.shared.exception.DomainException;

public class UserAlreadyExistsException extends DomainException {

    public UserAlreadyExistsException() {
        super("Username already exists.");
    }
}