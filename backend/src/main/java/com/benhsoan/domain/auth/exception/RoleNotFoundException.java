package com.benhsoan.domain.auth.exception;

import com.benhsoan.domain.shared.exception.DomainException;

public class RoleNotFoundException extends DomainException {

    public RoleNotFoundException() {
        super("Role not found.");
    }
}