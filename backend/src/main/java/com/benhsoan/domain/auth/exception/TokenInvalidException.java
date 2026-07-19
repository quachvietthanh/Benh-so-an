package com.benhsoan.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class TokenInvalidException extends DomainException {

    public TokenInvalidException() {
        super(
                HttpStatus.UNAUTHORIZED,
                "Token is invalid."
        );
    }
}