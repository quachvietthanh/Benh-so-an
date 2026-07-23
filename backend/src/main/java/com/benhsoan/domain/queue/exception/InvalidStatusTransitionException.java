package com.benhsoan.domain.queue.exception;

import org.springframework.http.HttpStatus;

import com.benhsoan.domain.queue.enums.QueueStatus;
import com.benhsoan.domain.shared.exception.DomainException;

public class InvalidStatusTransitionException
        extends DomainException {

    public InvalidStatusTransitionException(
            QueueStatus from,
            QueueStatus to
    ) {
        super(
                HttpStatus.BAD_REQUEST,
                String.format(
                        "Không thể chuyển từ trạng thái %s sang %s",
                        from, to
                )
        );
    }
}
