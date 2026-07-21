package com.benhsoan.port.inbound.patient;

import com.benhsoan.dto.command.patient.RegisterPatientCommand;
import com.benhsoan.dto.result.patient.PatientResult;

public interface RegisterPatientUseCase {

    PatientResult register(RegisterPatientCommand command);

}