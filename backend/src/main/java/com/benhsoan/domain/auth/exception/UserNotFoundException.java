package com.benhsoan.domain.auth.exception;

import com.benhsoan.domain.shared.exception.DomainException;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException() {
        super("User not found.");
    }

    public UserNotFoundException(String username) {
        super("User '" + username + "' not found.");
    }
}