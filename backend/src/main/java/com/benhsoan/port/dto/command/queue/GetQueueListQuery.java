package com.benhsoan.port.dto.command.queue;

import java.util.UUID;

import com.benhsoan.domain.queue.enums.QueueStatus;

public record GetQueueListQuery(

        String roomNumber,

        UUID doctorId,

        QueueStatus status,

        int page,

        int size

) {
    public GetQueueListQuery {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;
    }

    public static GetQueueListQuery byRoom(
            String roomNumber,
            QueueStatus status,
            Integer page,
            Integer size
    ) {
        return new GetQueueListQuery(
                roomNumber,
                null,
                status,
                page != null ? page : 0,
                size != null ? size : 20
        );
    }

    public static GetQueueListQuery byDoctor(
            UUID doctorId,
            QueueStatus status,
            Integer page,
            Integer size
    ) {
        return new GetQueueListQuery(
                null,
                doctorId,
                status,
                page != null ? page : 0,
                size != null ? size : 20
        );
    }
}
