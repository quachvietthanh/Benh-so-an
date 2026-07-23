package com.benhsoan.port.dto.command.queue;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CallNextCommand(

        @NotNull
        UUID doctorId,

        String roomNumber

) {
}
