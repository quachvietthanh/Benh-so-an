package com.benhsoan.port.dto.command.queue;

import java.util.UUID;

import com.benhsoan.domain.queue.enums.PriorityLevel;

import jakarta.validation.constraints.NotNull;

public record AddToQueueCommand(

        @NotNull
        UUID patientId,

        @NotNull
        PriorityLevel priorityLevel,

        String roomNumber,

        @NotNull
        UUID createdBy

) {
}
