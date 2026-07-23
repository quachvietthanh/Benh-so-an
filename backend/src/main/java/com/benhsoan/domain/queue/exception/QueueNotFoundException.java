package com.benhsoan.domain.queue.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.shared.exception.DomainException;

public class QueueNotFoundException
        extends DomainException {

    public QueueNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Không tìm thấy hàng đợi");
    }
}
