package com.benhsoan.application.ucservice.queue;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.queue.MedicalQueue;
import com.benhsoan.port.dto.command.queue.GetQueueListQuery;
import com.benhsoan.port.dto.result.PageResponse;
import com.benhsoan.port.dto.result.QueueResult;
import com.benhsoan.port.inbound.queue.GetQueueListUseCase;
import com.benhsoan.port.outbound.repository.crudRepository.queue.MedicalQueueRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQueueListService implements GetQueueListUseCase {

    private final MedicalQueueRepository medicalQueueRepository;

    @Override
    public PageResponse<QueueResult> getQueueList(GetQueueListQuery query) {

        long totalElements = count(query);

        if (totalElements == 0) {
            return PageResponse.empty(query.page(), query.size());
        }

        java.util.List<MedicalQueue> queues;

        if (query.roomNumber() != null) {
            queues = medicalQueueRepository.findByRoomNumberAndStatus(
                    query.roomNumber(),
                    query.status(),
                    query.page(),
                    query.size()
            );
        } else if (query.doctorId() != null) {
            queues = medicalQueueRepository.findByDoctorIdAndStatus(
                    query.doctorId(),
                    query.status(),
                    query.page(),
                    query.size()
            );
        } else {
            return PageResponse.empty(query.page(), query.size());
        }

        java.util.List<QueueResult> content = queues.stream()
                .map(QueueResult::from)
                .toList();

        return PageResponse.of(
                content,
                query.page(),
                query.size(),
                totalElements
        );
    }

    @Override
    public long count(GetQueueListQuery query) {

        if (query.roomNumber() != null) {
            if (query.status() != null) {
                return medicalQueueRepository.countByRoomNumberAndStatus(
                        query.roomNumber(),
                        query.status()
                );
            }
            return medicalQueueRepository.countByRoomNumberAndStatusIn(
                    query.roomNumber(),
                    java.util.List.of(
                            com.benhsoan.domain.queue.enums.QueueStatus.WAITING,
                            com.benhsoan.domain.queue.enums.QueueStatus.IN_PROGRESS,
                            com.benhsoan.domain.queue.enums.QueueStatus.WAITING_FOR_RESULT
                    )
            );
        }

        if (query.doctorId() != null) {
            if (query.status() != null) {
                return medicalQueueRepository.countByDoctorIdAndStatus(
                        query.doctorId(),
                        query.status()
                );
            }
            return medicalQueueRepository.countByDoctorIdAndStatusIn(
                    query.doctorId(),
                    java.util.List.of(
                            com.benhsoan.domain.queue.enums.QueueStatus.WAITING,
                            com.benhsoan.domain.queue.enums.QueueStatus.IN_PROGRESS,
                            com.benhsoan.domain.queue.enums.QueueStatus.WAITING_FOR_RESULT
                    )
            );
        }

        return 0;
    }
}
