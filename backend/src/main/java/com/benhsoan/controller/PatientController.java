package com.benhsoan.controller;

import com.benhsoan.dto.PatientDTO;
import com.benhsoan.service.PatientService;
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
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "API quản lý bệnh nhân")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "Lấy danh sách bệnh nhân", description = "Phân trang và tìm kiếm bệnh nhân")
    public ResponseEntity<Page<PatientDTO>> getAllPatients(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return ResponseEntity.ok(patientService.searchPatients(keyword, pageable));
        }
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin bệnh nhân theo ID")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Lấy thông tin bệnh nhân theo mã bệnh nhân")
    public ResponseEntity<PatientDTO> getPatientByCode(@PathVariable String code) {
        return ResponseEntity.ok(patientService.getPatientByCode(code));
    }

    @PostMapping
    @Operation(summary = "Thêm mới bệnh nhân")
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        PatientDTO created = patientService.createPatient(patientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin bệnh nhân")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO patientDTO) {
        return ResponseEntity.ok(patientService.updatePatient(id, patientDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa bệnh nhân")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
