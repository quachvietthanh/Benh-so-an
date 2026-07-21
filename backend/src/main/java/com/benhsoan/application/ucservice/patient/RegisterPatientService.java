package com.benhsoan.application.ucservice.patient;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.patient.Patient;
import com.benhsoan.domain.patient.PatientChangeLog;
import com.benhsoan.domain.patient.enums.PatientChangeAction;
import com.benhsoan.domain.patient.exception.PatientAlreadyExistsException;
import com.benhsoan.dto.request.patient.RegisterPatientCommand;
import com.benhsoan.port.inbound.patient.RegisterPatientUseCase;
import com.benhsoan.port.outbound.patient.PatientCodeGenerator;
import com.benhsoan.port.outbound.repository.crudRepository.patient.PatientRepository;
import com.benhsoan.port.outbound.repository.logRepository.PatientChangeLogRepository;
import com.benhsoan.port.outbound.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterPatientService
        implements RegisterPatientUseCase {

    private final PatientRepository patientRepository;

    private final PatientChangeLogRepository patientChangeLogRepository;

    private final PatientCodeGenerator patientCodeGenerator;

    private final CurrentUserProvider currentUserProvider;

    @Override
    public Patient register(RegisterPatientCommand command) {

        validate(command);

        UUID currentUserId =
                currentUserProvider.getCurrentUserId();

        String patientCode =
                patientCodeGenerator.generate();

        Patient patient =
                Patient.create(
                        patientCode,
                        command.fullName(),
                        command.dateOfBirth(),
                        command.gender(),
                        command.phone(),
                        command.email(),
                        command.address(),
                        command.identityNumber(),
                        command.insuranceNumber(),
                        command.bloodType(),
                        command.emergencyContact(),
                        command.emergencyPhone(),
                        currentUserId
                );

        Patient saved =
                patientRepository.save(patient);

        PatientChangeLog log =
                PatientChangeLog.create(
                        saved.getId(),
                        currentUserId,
                        PatientChangeAction.CREATE,
                        "Patient registered."
                );

        patientChangeLogRepository.save(log);

        return saved;
    }

    private void validate(RegisterPatientCommand command) {

        if (command.identityNumber() != null
                && patientRepository.existsByIdentityNumber(
                        command.identityNumber())) {

            throw new PatientAlreadyExistsException(
                    "identity number"
            );
        }

    }

}