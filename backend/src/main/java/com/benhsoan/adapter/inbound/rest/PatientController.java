package com.benhsoan.adapter.inbound.rest;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.benhsoan.domain.patient.Patient;
import com.benhsoan.dto.request.patient.RegisterPatientCommand;
import com.benhsoan.dto.request.patient.RegisterPatientRequest;
import com.benhsoan.dto.request.patient.UpdatePatientCommand;
import com.benhsoan.dto.request.patient.UpdatePatientRequest;
import com.benhsoan.dto.response.patient.PatientResponse;
import com.benhsoan.port.inbound.patient.RegisterPatientUseCase;
import com.benhsoan.port.inbound.patient.SearchPatientUseCase;
import com.benhsoan.port.inbound.patient.UpdatePatientUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Validated
public class PatientController {

    private final RegisterPatientUseCase registerPatientUseCase;

    private final SearchPatientUseCase searchPatientUseCase;

    private final UpdatePatientUseCase updatePatientUseCase;

    @PostMapping
    public PatientResponse register(
            @RequestBody RegisterPatientRequest request
    ) {

        Patient patient = registerPatientUseCase.register(

                RegisterPatientCommand.builder()
                        .fullName(request.fullName())
                        .dateOfBirth(request.dateOfBirth())
                        .gender(request.gender())
                        .phone(request.phone())
                        .email(request.email())
                        .address(request.address())
                        .identityNumber(request.identityNumber())
                        .insuranceNumber(request.insuranceNumber())
                        .bloodType(request.bloodType())
                        .emergencyContact(request.emergencyContact())
                        .emergencyPhone(request.emergencyPhone())
                        .build()

        );

        return PatientResponse.from(patient);
    }

    @GetMapping
    public Page<PatientResponse> search(

            @RequestParam(required = false)
            String keyword,

            Pageable pageable

    ) {

        return searchPatientUseCase
                .search(keyword, pageable)
                .map(PatientResponse::from);

    }

    @PutMapping("/{patientId}")
    public PatientResponse update(

            @PathVariable
            UUID patientId,

            @RequestBody
            UpdatePatientRequest request

    ) {

        Patient patient = updatePatientUseCase.update(

                patientId,

                UpdatePatientCommand.builder()
                        .fullName(request.fullName())
                        .dateOfBirth(request.dateOfBirth())
                        .gender(request.gender())
                        .phone(request.phone())
                        .email(request.email())
                        .address(request.address())
                        .identityNumber(request.identityNumber())
                        .insuranceNumber(request.insuranceNumber())
                        .bloodType(request.bloodType())
                        .emergencyContact(request.emergencyContact())
                        .emergencyPhone(request.emergencyPhone())
                        .active(request.active())
                        .build()

        );

        return PatientResponse.from(patient);
    }

}