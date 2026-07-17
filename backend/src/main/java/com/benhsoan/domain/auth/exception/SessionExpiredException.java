package com.benhsoan.domain.auth.exception;

import com.benhsoan.domain.shared.exception.DomainException;

public class SessionExpiredException extends DomainException {

    public SessionExpiredException() {
        super("Session has expired.");
    }
}