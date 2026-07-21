package com.benhsoan.application.ucservice.patient;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.patient.Patient;
import com.benhsoan.domain.patient.PatientChangeLog;
import com.benhsoan.domain.patient.enums.PatientChangeAction;
import com.benhsoan.domain.patient.exception.PatientAlreadyExistsException;
import com.benhsoan.domain.patient.exception.PatientNotFoundException;
import com.benhsoan.dto.request.patient.UpdatePatientCommand;
import com.benhsoan.port.inbound.patient.UpdatePatientUseCase;
import com.benhsoan.port.outbound.repository.crudRepository.patient.PatientRepository;
import com.benhsoan.port.outbound.repository.logRepository.PatientChangeLogRepository;
import com.benhsoan.port.outbound.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdatePatientService implements UpdatePatientUseCase {

    private final PatientRepository patientRepository;

    private final PatientChangeLogRepository patientChangeLogRepository;

    private final CurrentUserProvider currentUserProvider;

    @Override
    public Patient update(
            UUID patientId,
            UpdatePatientCommand command
    ) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        validate(patientId, command);

        UUID currentUserId = currentUserProvider.getCurrentUserId();

        patient.updateProfile(
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
                command.emergencyPhone()
        );

        if (command.active() && !patient.isActive()) {
            patient.activate();
        }

        if (!command.active() && patient.isActive()) {
            patient.deactivate();
        }

        Patient updatedPatient = patientRepository.save(patient);

        PatientChangeLog changeLog = PatientChangeLog.create(
                updatedPatient.getId(),
                currentUserId,
                PatientChangeAction.UPDATE,
                "Patient information updated."
        );

        patientChangeLogRepository.save(changeLog);

        return updatedPatient;
    }

    private void validate(
            UUID patientId,
            UpdatePatientCommand command
    ) {

        String identityNumber = command.identityNumber();

        if (identityNumber != null
                && patientRepository.existsByIdentityNumberAndIdNot(
                        identityNumber,
                        patientId
                )) {

            throw new PatientAlreadyExistsException(
                    "Identity number"
            );
        }

    }

}