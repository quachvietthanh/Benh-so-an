package com.benhsoan.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class SessionExpiredException extends DomainException {

    public SessionExpiredException() {
        super(
                HttpStatus.UNAUTHORIZED,
                "Session has expired."
        );
    }
}