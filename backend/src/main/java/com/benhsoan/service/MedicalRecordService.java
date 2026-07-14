package com.benhsoan.service;

import com.benhsoan.dto.MedicalRecordDTO;
import com.benhsoan.exception.ResourceNotFoundException;
import com.benhsoan.model.entity.MedicalRecord;
import com.benhsoan.model.entity.Patient;
import com.benhsoan.model.entity.User;
import com.benhsoan.repository.MedicalRecordRepository;
import com.benhsoan.repository.PatientRepository;
import com.benhsoan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public Page<MedicalRecordDTO> getAllRecords(Pageable pageable) {
        return medicalRecordRepository.findAll(pageable)
                .map(this::toDTO);
    }

    public MedicalRecordDTO getRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hồ sơ bệnh án", "id", id));
        return toDTO(record);
    }

    public Page<MedicalRecordDTO> getRecordsByPatientId(Long patientId, Pageable pageable) {
        return medicalRecordRepository.findByPatientId(patientId, pageable)
                .map(this::toDTO);
    }

    public Page<MedicalRecordDTO> getRecordsByDoctorId(Long doctorId, Pageable pageable) {
        return medicalRecordRepository.findByDoctorId(doctorId, pageable)
                .map(this::toDTO);
    }

    @Transactional
    public MedicalRecordDTO createRecord(MedicalRecordDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Bệnh nhân", "id", dto.getPatientId()));

        User doctor = null;
        if (dto.getDoctorId() != null) {
            doctor = userRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bác sĩ", "id", dto.getDoctorId()));
        }

        MedicalRecord record = MedicalRecord.builder()
                .recordCode(dto.getRecordCode())
                .patient(patient)
                .doctor(doctor)
                .diagnosis(dto.getDiagnosis())
                .symptoms(dto.getSymptoms())
                .treatment(dto.getTreatment())
                .prescription(dto.getPrescription())
                .notes(dto.getNotes())
                .status(dto.getStatus() != null
                        ? MedicalRecord.RecordStatus.valueOf(dto.getStatus())
                        : MedicalRecord.RecordStatus.NEW)
                .build();

        record = medicalRecordRepository.save(record);
        return toDTO(record);
    }

    @Transactional
    public MedicalRecordDTO updateRecord(Long id, MedicalRecordDTO dto) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hồ sơ bệnh án", "id", id));

        if (dto.getDiagnosis() != null) record.setDiagnosis(dto.getDiagnosis());
        if (dto.getSymptoms() != null) record.setSymptoms(dto.getSymptoms());
        if (dto.getTreatment() != null) record.setTreatment(dto.getTreatment());
        if (dto.getPrescription() != null) record.setPrescription(dto.getPrescription());
        if (dto.getNotes() != null) record.setNotes(dto.getNotes());
        if (dto.getStatus() != null) record.setStatus(MedicalRecord.RecordStatus.valueOf(dto.getStatus()));

        record = medicalRecordRepository.save(record);
        return toDTO(record);
    }

    @Transactional
    public void deleteRecord(Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hồ sơ bệnh án", "id", id);
        }
        medicalRecordRepository.deleteById(id);
    }

    private MedicalRecordDTO toDTO(MedicalRecord record) {
        return MedicalRecordDTO.builder()
                .id(record.getId())
                .recordCode(record.getRecordCode())
                .patientId(record.getPatient().getId())
                .patientName(record.getPatient().getFullName())
                .patientCode(record.getPatient().getPatientCode())
                .doctorId(record.getDoctor() != null ? record.getDoctor().getId() : null)
                .doctorName(record.getDoctor() != null ? record.getDoctor().getFullName() : null)
                .diagnosis(record.getDiagnosis())
                .symptoms(record.getSymptoms())
                .treatment(record.getTreatment())
                .prescription(record.getPrescription())
                .notes(record.getNotes())
                .status(record.getStatus() != null ? record.getStatus().name() : null)
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
