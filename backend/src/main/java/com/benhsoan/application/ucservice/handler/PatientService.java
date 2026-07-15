package com.benhsoan.service;

import com.benhsoan.dto.PatientDTO;
import com.benhsoan.exception.BadRequestException;
import com.benhsoan.exception.ResourceNotFoundException;
import com.benhsoan.model.entity.Patient;
import com.benhsoan.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;

    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(this::toDTO);
    }

    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bệnh nhân", "id", id));
        return toDTO(patient);
    }

    public PatientDTO getPatientByCode(String code) {
        Patient patient = patientRepository.findByPatientCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Bệnh nhân", "mã bệnh nhân", code));
        return toDTO(patient);
    }

    public Page<PatientDTO> searchPatients(String keyword, Pageable pageable) {
        return patientRepository.searchPatients(keyword, pageable)
                .map(this::toDTO);
    }

    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO) {
        if (patientRepository.existsByPatientCode(patientDTO.getPatientCode())) {
            throw new BadRequestException("Mã bệnh nhân đã tồn tại");
        }

        Patient patient = toEntity(patientDTO);
        patient = patientRepository.save(patient);
        return toDTO(patient);
    }

    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bệnh nhân", "id", id));

        updateEntity(existingPatient, patientDTO);
        existingPatient = patientRepository.save(existingPatient);
        return toDTO(existingPatient);
    }

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bệnh nhân", "id", id);
        }
        patientRepository.deleteById(id);
    }

    // ===== Mapper methods =====

    private PatientDTO toDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .patientCode(patient.getPatientCode())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender() != null ? patient.getGender().name() : null)
                .phoneNumber(patient.getPhoneNumber())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .identityNumber(patient.getIdentityNumber())
                .healthInsuranceCode(patient.getHealthInsuranceCode())
                .bloodType(patient.getBloodType())
                .emergencyContact(patient.getEmergencyContact())
                .emergencyPhone(patient.getEmergencyPhone())
                .medicalHistory(patient.getMedicalHistory())
                .allergies(patient.getAllergies())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }

    private Patient toEntity(PatientDTO dto) {
        return Patient.builder()
                .patientCode(dto.getPatientCode())
                .fullName(dto.getFullName())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender() != null ? Patient.Gender.valueOf(dto.getGender()) : null)
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .identityNumber(dto.getIdentityNumber())
                .healthInsuranceCode(dto.getHealthInsuranceCode())
                .bloodType(dto.getBloodType())
                .emergencyContact(dto.getEmergencyContact())
                .emergencyPhone(dto.getEmergencyPhone())
                .medicalHistory(dto.getMedicalHistory())
                .allergies(dto.getAllergies())
                .build();
    }

    private void updateEntity(Patient patient, PatientDTO dto) {
        if (dto.getFullName() != null) patient.setFullName(dto.getFullName());
        if (dto.getDateOfBirth() != null) patient.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getGender() != null) patient.setGender(Patient.Gender.valueOf(dto.getGender()));
        if (dto.getPhoneNumber() != null) patient.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail() != null) patient.setEmail(dto.getEmail());
        if (dto.getAddress() != null) patient.setAddress(dto.getAddress());
        if (dto.getIdentityNumber() != null) patient.setIdentityNumber(dto.getIdentityNumber());
        if (dto.getHealthInsuranceCode() != null) patient.setHealthInsuranceCode(dto.getHealthInsuranceCode());
        if (dto.getBloodType() != null) patient.setBloodType(dto.getBloodType());
        if (dto.getEmergencyContact() != null) patient.setEmergencyContact(dto.getEmergencyContact());
        if (dto.getEmergencyPhone() != null) patient.setEmergencyPhone(dto.getEmergencyPhone());
        if (dto.getMedicalHistory() != null) patient.setMedicalHistory(dto.getMedicalHistory());
        if (dto.getAllergies() != null) patient.setAllergies(dto.getAllergies());
    }
}
