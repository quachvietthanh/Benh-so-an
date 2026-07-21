package com.benhsoan.port.inbound.patient;

import java.util.UUID;

import com.benhsoan.domain.patient.Patient;

public interface UpdatePatientUseCase {

    Patient update(
            UUID patientId,
            UpdatePatientCommand command
    );

}