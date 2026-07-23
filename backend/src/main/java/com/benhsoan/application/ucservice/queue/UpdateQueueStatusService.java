package com.benhsoan.application.ucservice.queue;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.queue.MedicalQueue;
import com.benhsoan.domain.queue.exception.QueueNotFoundException;
import com.benhsoan.port.dto.command.queue.UpdateQueueStatusCommand;
import com.benhsoan.port.dto.result.QueueResult;
import com.benhsoan.port.inbound.queue.UpdateQueueStatusUseCase;
import com.benhsoan.port.outbound.repository.crudRepository.queue.MedicalQueueRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateQueueStatusService
        implements UpdateQueueStatusUseCase {

    private final MedicalQueueRepository medicalQueueRepository;

    @Override
    public QueueResult updateStatus(UpdateQueueStatusCommand command) {

        MedicalQueue queue = medicalQueueRepository
                .findById(command.queueId())
                .orElseThrow(QueueNotFoundException::new);

        switch (command.newStatus()) {
            case WAITING:
                // No transition needed — already WAITING
                break;
            case IN_PROGRESS:
                queue.call(command.doctorId());
                break;
            case WAITING_FOR_RESULT:
                queue.sendToWaitingForResult();
                break;
            case SKIPPED:
                queue.skip();
                break;
            case COMPLETED:
                queue.complete();
                break;
            case CANCELLED:
                queue.cancel(command.cancelReason());
                break;
        }

        MedicalQueue saved = medicalQueueRepository.save(queue);

        return QueueResult.from(saved);
    }
}
