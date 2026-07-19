package com.benhsoan.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException() {
        super(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password."
        );
    }
}