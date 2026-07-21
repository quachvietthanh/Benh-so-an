package com.benhsoan.port.inbound.patient;

import java.util.UUID;

import com.benhsoan.dto.command.patient.UpdatePatientCommand;
import com.benhsoan.dto.result.patient.PatientResult;
public interface UpdatePatientUseCase {

    PatientResult update(
            UUID patientId,
            UpdatePatientCommand command
    );

}