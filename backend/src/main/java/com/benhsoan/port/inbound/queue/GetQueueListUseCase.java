package com.benhsoan.port.inbound.queue;

import com.benhsoan.port.dto.command.queue.GetQueueListQuery;
import com.benhsoan.port.dto.result.PageResponse;
import com.benhsoan.port.dto.result.QueueResult;

public interface GetQueueListUseCase {

    PageResponse<QueueResult> getQueueList(GetQueueListQuery query);

    long count(GetQueueListQuery query);
}
