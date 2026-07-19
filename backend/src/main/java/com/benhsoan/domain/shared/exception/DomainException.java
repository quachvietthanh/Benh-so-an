package com.benhsoan.domain.shared.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {

    private final HttpStatus status;

    protected DomainException(
            HttpStatus status,
            String message
    ) {
        super(message);
        this.status = status;
    }

}