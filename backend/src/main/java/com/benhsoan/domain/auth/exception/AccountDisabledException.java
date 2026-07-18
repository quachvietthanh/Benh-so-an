package com.benhsoan.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class AccountDisabledException extends DomainException {

    public AccountDisabledException() {
        super(
                HttpStatus.FORBIDDEN,
                "Account has been disabled."
        );
    }
}