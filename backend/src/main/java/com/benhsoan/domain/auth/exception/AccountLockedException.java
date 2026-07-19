package com.benhsoan.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class AccountLockedException extends DomainException {

    public AccountLockedException() {
        super(
                HttpStatus.FORBIDDEN,
                "Account has been locked."
        );
    }
}