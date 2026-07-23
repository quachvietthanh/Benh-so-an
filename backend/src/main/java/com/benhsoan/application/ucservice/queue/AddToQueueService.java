package com.benhsoan.application.ucservice.queue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.queue.MedicalQueue;
import com.benhsoan.port.dto.command.queue.AddToQueueCommand;
import com.benhsoan.port.dto.result.QueueResult;
import com.benhsoan.port.inbound.queue.AddToQueueUseCase;
import com.benhsoan.port.outbound.repository.crudRepository.queue.MedicalQueueRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AddToQueueService implements AddToQueueUseCase {

    private final MedicalQueueRepository medicalQueueRepository;

    private static final int MAX_RETRIES = 3;

    @Override
    public QueueResult addToQueue(AddToQueueCommand command) {

        RuntimeException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return doAddToQueue(command);
            } catch (DataIntegrityViolationException e) {
                lastException = e;
                if (attempt == MAX_RETRIES) {
                    throw e;
                }
            }
        }

        throw lastException;
    }

    private QueueResult doAddToQueue(AddToQueueCommand command) {

        Instant startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant endOfDay = LocalDate.now()
                .atTime(LocalTime.MAX)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        int nextNumber = medicalQueueRepository
                .findMaxQueueNumberForToday(
                        command.roomNumber(),
                        startOfDay,
                        endOfDay
                ) + 1;

        MedicalQueue queue = MedicalQueue.create(
                command.patientId(),
                nextNumber,
                command.priorityLevel(),
                command.roomNumber(),
                command.createdBy()
        );

        MedicalQueue saved = medicalQueueRepository.save(queue);

        return QueueResult.from(saved);
    }
}
