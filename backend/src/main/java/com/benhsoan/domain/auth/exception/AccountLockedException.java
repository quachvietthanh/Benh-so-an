package com.benhsoan.domain.auth.exception;

import com.benhsoan.domain.shared.exception.DomainException;

public class AccountLockedException extends DomainException {

    public AccountLockedException() {
        super("Account is temporarily locked.");
    }
}