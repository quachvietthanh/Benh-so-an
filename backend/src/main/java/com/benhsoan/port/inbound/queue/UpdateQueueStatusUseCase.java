package com.benhsoan.port.inbound.queue;

import com.benhsoan.port.dto.command.queue.UpdateQueueStatusCommand;
import com.benhsoan.port.dto.result.QueueResult;

public interface UpdateQueueStatusUseCase {

    QueueResult updateStatus(UpdateQueueStatusCommand command);
}
