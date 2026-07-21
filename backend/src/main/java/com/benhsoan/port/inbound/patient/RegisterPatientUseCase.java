package com.benhsoan.port.inbound.patient;

import com.benhsoan.domain.patient.Patient;
import com.benhsoan.dto.request.patient.RegisterPatientCommand;

public interface RegisterPatientUseCase {

    Patient register(RegisterPatientCommand command);

}