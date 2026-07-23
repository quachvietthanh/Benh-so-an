package com.benhsoan.application.ucservice.queue;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.queue.MedicalQueue;
import com.benhsoan.domain.queue.exception.QueueNotFoundException;
import com.benhsoan.port.dto.command.queue.CallNextCommand;
import com.benhsoan.port.dto.result.QueueResult;
import com.benhsoan.port.inbound.queue.CallNextUseCase;
import com.benhsoan.port.outbound.repository.crudRepository.queue.MedicalQueueRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CallNextService implements CallNextUseCase {

    private final MedicalQueueRepository medicalQueueRepository;

    @Override
    public QueueResult callNext(CallNextCommand command) {

        MedicalQueue next = medicalQueueRepository
                .findNextWaiting(command.roomNumber())
                .orElseThrow(QueueNotFoundException::new);

        next.call(command.doctorId());

        MedicalQueue saved = medicalQueueRepository.save(next);

        return QueueResult.from(saved);
    }
}
