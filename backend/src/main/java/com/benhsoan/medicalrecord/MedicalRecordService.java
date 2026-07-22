package com.benhsoan.medicalrecord;

import java.time.Instant;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.benhsoan.persistence.entity.auth.UserEntity;
import com.benhsoan.persistence.entity.patient.PatientEntity;
import com.benhsoan.persistence.jpaRepository.auth.JpaUserRepository;
import com.benhsoan.persistence.jpaRepository.patient.JpaPatientRepository;
import com.benhsoan.port.outbound.security.CurrentUserProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordService {
    private static final Set<String> ALLOWED_TYPES = Set.of("application/pdf", "image/jpeg", "image/png");
    private final MedicalRecordRepository repository;
    private final MedicalRecordAttachmentRepository attachmentRepository;
    private final JpaPatientRepository patientRepository;
    private final JpaUserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getAll(UUID patientId) {
        List<MedicalRecordEntity> records = patientId == null
                ? repository.findAllByOrderByCreatedAtDesc()
                : repository.findByPatientIdOrderByCreatedAtDesc(patientId);
        return records.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse get(UUID id) { MedicalRecordEntity entity=requireRecord(id); audit(entity,"VIEW"); return toResponse(entity); }

    public MedicalRecordResponse create(MedicalRecordRequest request) {
        patientRepository.findById(request.patientId())
                .filter(PatientEntity::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hồ sơ bệnh nhân không hợp lệ"));
        UUID doctorId = currentUserProvider.getCurrentUserId();
        Instant now = Instant.now();
        MedicalRecordEntity entity = MedicalRecordEntity.builder()
                .id(UUID.randomUUID()).recordCode("BA-" + now.toEpochMilli())
                .patientId(request.patientId()).doctorId(doctorId)
                .symptoms(request.symptoms().trim()).examinationNote(request.examinationNote())
                .diagnosis(request.diagnosis().trim()).treatmentPlan(request.treatmentPlan())
                .clinicalOrders(write(request.clinicalOrders() == null ? List.of() : request.clinicalOrders()))
                .clinicalResults(write(request.clinicalResults() == null ? Map.of() : request.clinicalResults()))
                .status("COMPLETED").createdAt(now).updatedAt(now).build();
        MedicalRecordEntity saved=repository.save(entity); audit(saved,"CREATE"); return toResponse(saved);
    }

    public MedicalRecordResponse update(UUID id, MedicalRecordRequest request) {
        MedicalRecordEntity entity = requireOwnedRecord(id);
        entity.setSymptoms(request.symptoms().trim());
        entity.setExaminationNote(request.examinationNote());
        entity.setDiagnosis(request.diagnosis().trim());
        entity.setTreatmentPlan(request.treatmentPlan());
        entity.setClinicalOrders(write(request.clinicalOrders() == null ? List.of() : request.clinicalOrders()));
        entity.setClinicalResults(write(request.clinicalResults() == null ? Map.of() : request.clinicalResults()));
        entity.setUpdatedAt(Instant.now());
        MedicalRecordEntity saved=repository.save(entity); audit(saved,"UPDATE"); return toResponse(saved);
    }

    public MedicalRecordResponse attach(UUID recordId, MultipartFile file) {
        requireOwnedRecord(recordId);
        if (file.isEmpty() || file.getSize() > 10 * 1024 * 1024) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tệp phải có dung lượng từ 1 byte đến 10 MB");
        if (!ALLOWED_TYPES.contains(file.getContentType())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ chấp nhận PDF, JPG hoặc PNG");
        try {
            attachmentRepository.save(MedicalRecordAttachmentEntity.builder()
                    .id(UUID.randomUUID()).recordId(recordId).fileName(Objects.requireNonNullElse(file.getOriginalFilename(), "attachment"))
                    .contentType(file.getContentType()).fileSize(file.getSize()).fileData(file.getBytes()).uploadedAt(Instant.now()).build());
            return toResponse(requireRecord(recordId));
        } catch (java.io.IOException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể đọc tệp đính kèm");
        }
    }

    @Transactional(readOnly = true)
    public MedicalRecordAttachmentEntity getAttachment(UUID id) {
        return attachmentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tệp"));
    }

    private MedicalRecordEntity requireRecord(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bệnh án"));
    }

    private void audit(MedicalRecordEntity entity, String action) {
        jdbcTemplate.update("INSERT INTO emr_audit_logs VALUES(UUID_TO_BIN(UUID()),UUID_TO_BIN(?),UUID_TO_BIN(?),UUID_TO_BIN(?),?,CURRENT_TIMESTAMP)", entity.getId().toString(), entity.getPatientId().toString(), currentUserProvider.getCurrentUserId().toString(), action);
    }

    private MedicalRecordEntity requireOwnedRecord(UUID id) {
        MedicalRecordEntity entity = requireRecord(id);
        if (!entity.getDoctorId().equals(currentUserProvider.getCurrentUserId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ bác sĩ lập bệnh án mới được bổ sung nội dung");
        return entity;
    }

    private String write(Object value) {
        try { return objectMapper.writeValueAsString(value); }
        catch (Exception error) { throw new IllegalStateException("Không thể lưu dữ liệu JSON", error); }
    }

    private MedicalRecordResponse toResponse(MedicalRecordEntity entity) {
        String patientName = patientRepository.findById(entity.getPatientId()).map(PatientEntity::getFullName).orElse("---");
        String doctorName = userRepository.findById(entity.getDoctorId()).map(UserEntity::getFullName).orElse("---");
        List<String> orders = read(entity.getClinicalOrders(), new TypeReference<>() {}, List.of());
        Map<String, String> results = read(entity.getClinicalResults(), new TypeReference<>() {}, Map.of());
        var attachments = attachmentRepository.findByRecordIdOrderByUploadedAt(entity.getId()).stream()
                .map(item -> new MedicalRecordResponse.AttachmentInfo(item.getId(), item.getFileName(), item.getContentType(), item.getFileSize())).toList();
        return new MedicalRecordResponse(entity.getId(), entity.getRecordCode(), entity.getPatientId(), patientName,
                entity.getDoctorId(), doctorName, entity.getSymptoms(), entity.getExaminationNote(), entity.getDiagnosis(),
                entity.getTreatmentPlan(), orders, results, entity.getStatus(), entity.getCreatedAt(), attachments);
    }

    private <T> T read(String json, TypeReference<T> type, T fallback) {
        if (json == null || json.isBlank()) return fallback;
        try { return objectMapper.readValue(json, type); }
        catch (Exception error) { return fallback; }
    }
}
