package com.benhsoan.adapter.inbound.rest.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.benhsoan.adapter.inbound.rest.response.patient.MedicalHistoryItemResponse;
import com.benhsoan.domain.auth.User;
import com.benhsoan.domain.patient.Visit;
import com.benhsoan.port.outbound.repository.crudRepository.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MedicalHistoryRestMapper {

    private final UserRepository userRepository;

    public Page<MedicalHistoryItemResponse> toResponse(Page<Visit> visits) {

        List<UUID> doctorIds = visits.getContent().stream()
                .map(Visit::getDoctorId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<UUID, String> doctorNames;

        if (doctorIds.isEmpty()) {
            doctorNames = Collections.emptyMap();
        } else {
            doctorNames = userRepository.findAllById(doctorIds).stream()
                    .collect(Collectors.toMap(
                            User::getId,
                            User::getFullName
                    ));
        }

        return visits.map(visit -> toResponse(visit, doctorNames));
    }

    private MedicalHistoryItemResponse toResponse(
            Visit visit,
            Map<UUID, String> doctorNames
    ) {
        return new MedicalHistoryItemResponse(
                visit.getId(),
                visit.getVisitCode(),
                visit.getVisitType(),
                visit.getVisitStatus(),
                visit.getVisitAt(),
                visit.getReason(),
                visit.getNote(),
                visit.getDoctorId(),
                doctorNames.get(visit.getDoctorId()),
                visit.getDepartmentId()
        );
    }
}
