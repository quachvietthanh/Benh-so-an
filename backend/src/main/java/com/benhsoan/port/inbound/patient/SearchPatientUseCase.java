package com.benhsoan.port.inbound.patient;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.benhsoan.port.dto.result.PatientResult;

public interface SearchPatientUseCase {

    PatientResult getById(UUID patientId);

    Page<PatientResult> search(
            String keyword,
            Pageable pageable
    );

}
