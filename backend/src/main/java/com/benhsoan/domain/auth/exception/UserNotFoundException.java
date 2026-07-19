package com.benhsoan.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException() {
        super(
                HttpStatus.NOT_FOUND,
                "User not found."
        );
    }

    public UserNotFoundException(String username) {
        super(
                HttpStatus.NOT_FOUND,
                "User '" + username + "' not found."
        );
    }
}