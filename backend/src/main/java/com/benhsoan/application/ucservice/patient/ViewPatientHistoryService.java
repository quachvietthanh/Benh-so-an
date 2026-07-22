package com.benhsoan.application.ucservice.patient;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.patient.Visit;
import com.benhsoan.port.inbound.patient.ViewPatientHistoryUseCase;
import com.benhsoan.port.outbound.repository.crudRepository.patient.VisitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewPatientHistoryService implements ViewPatientHistoryUseCase {
    private final VisitRepository visitRepository;

    @Override
    public Page<Visit> getVisitHistory(UUID patientId, Pageable pageable) {
        return visitRepository.findByPatientId(patientId, pageable);
    }
}
