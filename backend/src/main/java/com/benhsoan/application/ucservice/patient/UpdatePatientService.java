package com.benhsoan.application.ucservice.patient;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.benhsoan.domain.patient.Patient;
import com.benhsoan.domain.patient.PatientChangeLog;
import com.benhsoan.domain.patient.enums.PatientChangeAction;
import com.benhsoan.domain.patient.exception.PatientAlreadyExistsException;
import com.benhsoan.domain.patient.exception.PatientNotFoundException;
import com.benhsoan.port.dto.command.patient.UpdatePatientCommand;
import com.benhsoan.port.dto.result.PatientResult;
import com.benhsoan.port.inbound.patient.UpdatePatientUseCase;
import com.benhsoan.port.outbound.repository.crudRepository.patient.PatientRepository;
import com.benhsoan.port.outbound.repository.logRepository.PatientChangeLogRepository;
import com.benhsoan.port.outbound.security.CurrentUserProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdatePatientService
        implements UpdatePatientUseCase {

    private final PatientRepository patientRepository;

    private final PatientChangeLogRepository patientChangeLogRepository;

    private final CurrentUserProvider currentUserProvider;

    private final ObjectMapper objectMapper;
    private final PatientResultMapper patientResultMapper;

    @Override
    public PatientResult update( UUID patientId, UpdatePatientCommand command ) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        validate(
                patientId,
                command
        );

        UUID currentUserId =
                currentUserProvider.getCurrentUserId();

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

        Patient updatedPatient =
                patientRepository.save(patient);

        PatientChangeLog changeLog =
                PatientChangeLog.create(
                        updatedPatient.getId(),
                        currentUserId,
                        PatientChangeAction.UPDATE,
                        createChangeDetail()
                );

        patientChangeLogRepository.save(changeLog);

        return patientResultMapper.toResult(updatedPatient);
    }

    private void validate(
            UUID patientId,
            UpdatePatientCommand command
    ) {

        String identityNumber =
                command.identityNumber();

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

    private String createChangeDetail() {

        try {

            return objectMapper.writeValueAsString(
                    Map.of(
                            "message",
                            "Patient information updated."
                    )
            );

        } catch (JsonProcessingException ex) {

            throw new IllegalStateException(
                    "Cannot serialize patient change detail.",
                    ex
            );
        }
    }

}