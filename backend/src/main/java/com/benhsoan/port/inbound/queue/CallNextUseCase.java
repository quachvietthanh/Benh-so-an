package com.benhsoan.port.inbound.queue;

import com.benhsoan.port.dto.command.queue.CallNextCommand;
import com.benhsoan.port.dto.result.QueueResult;

public interface CallNextUseCase {

    QueueResult callNext(CallNextCommand command);
}
