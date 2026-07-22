package com.benhsoan.application.ucservice.patient;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.patient.Patient;
import com.benhsoan.domain.patient.exception.PatientNotFoundException;
import com.benhsoan.port.dto.result.PatientResult;
import com.benhsoan.port.inbound.patient.SearchPatientUseCase;
import com.benhsoan.port.outbound.repository.crudRepository.patient.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchPatientService
        implements SearchPatientUseCase {

    private final PatientRepository patientRepository;
    private final PatientResultMapper patientResultMapper;

    @Override
    public PatientResult getById(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));
        return patientResultMapper.toResult(patient);
    }

    @Override
    public Page<PatientResult> search(
        String keyword,
        Pageable pageable
    ) {

        Page<Patient> patients;

        if (keyword == null || keyword.isBlank()) {
            patients = patientRepository.findAll(pageable);
        } else if (keyword.trim().toUpperCase().startsWith("BN")) {
            var patient = patientRepository.findByPatientCode(keyword.trim());
            patients = patient.<Page<Patient>>map(value ->
                    new PageImpl<>(java.util.List.of(value), pageable, 1))
                    .orElseGet(() -> Page.empty(pageable));
        } else {
            patients = patientRepository.findByFullNameContaining(
                keyword.trim(),
                pageable
            );
        }

        return patientResultMapper.toResult(patients);
    }
}
