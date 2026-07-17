package com.benhsoan.domain.auth.exception;

import com.benhsoan.domain.shared.exception.DomainException;

public class AccountDisabledException extends DomainException {

    public AccountDisabledException() {
        super("Account has been disabled.");
    }
}