package com.benhsoan.domain.auth.exception;

import com.benhsoan.domain.shared.exception.DomainException;

public class TokenInvalidException extends DomainException {

    public TokenInvalidException() {
        super("Invalid access token.");
    }
}