package com.benhsoan.port.inbound.queue;

import com.benhsoan.port.dto.command.queue.AddToQueueCommand;
import com.benhsoan.port.dto.result.QueueResult;

public interface AddToQueueUseCase {

    QueueResult addToQueue(AddToQueueCommand command);
}
