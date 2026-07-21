package com.benhsoan.port.inbound.patient;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.benhsoan.domain.patient.Visit;

public interface ViewPatientHistoryUseCase {

    Page<Visit> getVisitHistory(
            UUID patientId,
            Pageable pageable
    );

}