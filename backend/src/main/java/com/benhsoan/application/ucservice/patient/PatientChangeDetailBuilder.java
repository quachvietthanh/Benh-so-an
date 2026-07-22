package com.benhsoan.application.ucservice.patient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.benhsoan.domain.patient.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PatientChangeDetailBuilder {

    private final ObjectMapper objectMapper;

    public String forCreate(Patient patient) {

        Map<String, Object> values = new LinkedHashMap<>();

        values.put("patientCode", patient.getPatientCode());
        values.put("fullName", patient.getFullName());
        values.put("dateOfBirth", patient.getDateOfBirth());
        values.put("gender", patient.getGender());
        values.put("phone", patient.getPhone());
        values.put("email", patient.getEmail());
        values.put("address", patient.getAddress());
        values.put("identityNumber", patient.getIdentityNumber());
        values.put("insuranceNumber", patient.getInsuranceNumber());
        values.put("bloodType", patient.getBloodType());
        values.put("emergencyContact", patient.getEmergencyContact());
        values.put("emergencyPhone", patient.getEmergencyPhone());
        values.put("active", patient.isActive());

        Map<String, Object> detail = new LinkedHashMap<>();

        detail.put("action", "CREATE");
        detail.put("entity", "PATIENT");
        detail.put("values", values);

        return toJson(detail);
    }

    public String forUpdate(
            Patient oldPatient,
            Patient newPatient
    ) {

        List<Map<String, Object>> changes = new ArrayList<>();

        addChange(changes, "fullName",
                oldPatient.getFullName(),
                newPatient.getFullName());

        addChange(changes, "dateOfBirth",
                oldPatient.getDateOfBirth(),
                newPatient.getDateOfBirth());

        addChange(changes, "gender",
                oldPatient.getGender(),
                newPatient.getGender());

        addChange(changes, "phone",
                oldPatient.getPhone(),
                newPatient.getPhone());

        addChange(changes, "email",
                oldPatient.getEmail(),
                newPatient.getEmail());

        addChange(changes, "address",
                oldPatient.getAddress(),
                newPatient.getAddress());

        addChange(changes, "identityNumber",
                oldPatient.getIdentityNumber(),
                newPatient.getIdentityNumber());

        addChange(changes, "insuranceNumber",
                oldPatient.getInsuranceNumber(),
                newPatient.getInsuranceNumber());

        addChange(changes, "bloodType",
                oldPatient.getBloodType(),
                newPatient.getBloodType());

        addChange(changes, "emergencyContact",
                oldPatient.getEmergencyContact(),
                newPatient.getEmergencyContact());

        addChange(changes, "emergencyPhone",
                oldPatient.getEmergencyPhone(),
                newPatient.getEmergencyPhone());

        addChange(changes, "active",
                oldPatient.isActive(),
                newPatient.isActive());

        Map<String, Object> detail = new LinkedHashMap<>();

        detail.put("action", "UPDATE");
        detail.put("entity", "PATIENT");
        detail.put("changes", changes);

        return toJson(detail);
    }

    private void addChange(
            List<Map<String, Object>> changes,
            String field,
            Object oldValue,
            Object newValue
    ) {

        if (Objects.equals(oldValue, newValue)) {
            return;
        }

        Map<String, Object> item = new LinkedHashMap<>();

        item.put("field", field);
        item.put("oldValue", oldValue);
        item.put("newValue", newValue);

        changes.add(item);
    }

    private String toJson(Map<String, Object> detail) {

        try {

            return objectMapper.writeValueAsString(detail);

        } catch (JsonProcessingException ex) {

            throw new IllegalStateException(
                    "Cannot serialize patient change detail.",
                    ex
            );
        }
    }

}