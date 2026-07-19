package com.benhsoan.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class UserAlreadyExistsException extends DomainException {

    public UserAlreadyExistsException() {
        super(
                HttpStatus.CONFLICT,
                "Username already exists."
        );
    }
}