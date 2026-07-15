package com.benhsoan.controller;

import com.benhsoan.dto.MedicalRecordDTO;
import com.benhsoan.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
@Tag(name = "Medical Record", description = "API quản lý hồ sơ bệnh án")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping
    @Operation(summary = "Lấy danh sách hồ sơ bệnh án")
    public ResponseEntity<Page<MedicalRecordDTO>> getAllRecords(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(medicalRecordService.getAllRecords(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin hồ sơ bệnh án theo ID")
    public ResponseEntity<MedicalRecordDTO> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.getRecordById(id));
    }

    @GetMapping("/by-patient/{patientId}")
    @Operation(summary = "Lấy hồ sơ bệnh án theo bệnh nhân")
    public ResponseEntity<Page<MedicalRecordDTO>> getRecordsByPatient(
            @PathVariable Long patientId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByPatientId(patientId, pageable));
    }

    @GetMapping("/by-doctor/{doctorId}")
    @Operation(summary = "Lấy hồ sơ bệnh án theo bác sĩ")
    public ResponseEntity<Page<MedicalRecordDTO>> getRecordsByDoctor(
            @PathVariable Long doctorId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByDoctorId(doctorId, pageable));
    }

    @PostMapping
    @Operation(summary = "Tạo hồ sơ bệnh án mới")
    public ResponseEntity<MedicalRecordDTO> createRecord(@Valid @RequestBody MedicalRecordDTO dto) {
        MedicalRecordDTO created = medicalRecordService.createRecord(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật hồ sơ bệnh án")
    public ResponseEntity<MedicalRecordDTO> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordDTO dto) {
        return ResponseEntity.ok(medicalRecordService.updateRecord(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa hồ sơ bệnh án")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        medicalRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
