package com.benhsoan.port.inbound.patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.benhsoan.domain.patient.Patient;

public interface SearchPatientUseCase {

    Page<Patient> search(
            String keyword,
            Pageable pageable
    );

}