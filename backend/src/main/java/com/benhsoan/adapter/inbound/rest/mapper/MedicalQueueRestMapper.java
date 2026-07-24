package com.benhsoan.adapter.inbound.rest.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.benhsoan.adapter.inbound.rest.request.queue.AddToQueueRequest;
import com.benhsoan.adapter.inbound.rest.request.queue.CallNextRequest;
import com.benhsoan.adapter.inbound.rest.request.queue.UpdateQueueStatusRequest;
import com.benhsoan.adapter.inbound.rest.response.queue.MedicalQueueResponse;
import com.benhsoan.domain.auth.User;
import com.benhsoan.port.dto.command.queue.AddToQueueCommand;
import com.benhsoan.port.dto.command.queue.CallNextCommand;
import com.benhsoan.port.dto.command.queue.UpdateQueueStatusCommand;
import com.benhsoan.port.dto.result.PageResponse;
import com.benhsoan.port.dto.result.QueueResult;
import com.benhsoan.port.outbound.repository.crudRepository.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MedicalQueueRestMapper {

    private final UserRepository userRepository;

    public AddToQueueCommand toCommand(
            AddToQueueRequest request,
            UUID createdBy
    ) {
        return new AddToQueueCommand(
                request.patientId(),
                request.priorityLevel(),
                request.roomNumber(),
                request.doctorId(),
                createdBy
        );
    }

    public CallNextCommand toCommand(CallNextRequest request) {
        return new CallNextCommand(
                request.doctorId(),
                request.roomNumber()
        );
    }

    public UpdateQueueStatusCommand toCommand(
            UUID queueId,
            UpdateQueueStatusRequest request
    ) {
        return new UpdateQueueStatusCommand(
                queueId,
                request.newStatus(),
                request.doctorId(),
                request.cancelReason()
        );
    }

    public PageResponse<MedicalQueueResponse> toPageResponse(
            PageResponse<QueueResult> resultPage
    ) {
        List<MedicalQueueResponse> content;

        if (resultPage.content().isEmpty()) {
            content = List.of();
        } else {
            List<UUID> patientIds = resultPage.content().stream()
                    .map(QueueResult::patientId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            List<UUID> doctorIds = resultPage.content().stream()
                    .map(QueueResult::doctorId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            Map<UUID, String> patientNames = loadUserNames(patientIds);
            Map<UUID, String> doctorNames = loadUserNames(doctorIds);

            content = resultPage.content().stream()
                    .map(r -> toResponse(r, patientNames, doctorNames))
                    .toList();
        }

        return new PageResponse<>(
                content,
                resultPage.page(),
                resultPage.size(),
                resultPage.totalElements(),
                resultPage.totalPages()
        );
    }

    public MedicalQueueResponse toResponse(QueueResult result) {
        return toResponse(result, Collections.emptyMap(), Collections.emptyMap());
    }

    public List<MedicalQueueResponse> toResponse(List<QueueResult> results) {

        if (results.isEmpty()) {
            return List.of();
        }

        // Batch-load patient names and doctor names — 2 queries, not N+1
        List<UUID> patientIds = results.stream()
                .map(QueueResult::patientId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<UUID> doctorIds = results.stream()
                .map(QueueResult::doctorId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<UUID, String> patientNames = loadUserNames(patientIds);
        Map<UUID, String> doctorNames = loadUserNames(doctorIds);

        return results.stream()
                .map(r -> toResponse(r, patientNames, doctorNames))
                .toList();
    }

    private MedicalQueueResponse toResponse(
            QueueResult result,
            Map<UUID, String> patientNames,
            Map<UUID, String> doctorNames
    ) {
        return new MedicalQueueResponse(
                result.id(),
                result.patientId(),
                patientNames.get(result.patientId()),
                result.doctorId(),
                doctorNames.get(result.doctorId()),
                result.roomNumber(),
                result.queueNumber(),
                result.status(),
                result.priorityLevel(),
                result.notes(),
                result.checkedInAt(),
                result.calledAt(),
                result.startedAt(),
                result.waitingForResultAt(),
                result.completedAt(),
                result.cancelledAt(),
                result.cancelReason(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    private Map<UUID, String> loadUserNames(List<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        User::getFullName
                ));
    }
}
