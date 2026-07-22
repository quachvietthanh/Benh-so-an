package com.benhsoan.medicalrecord;

import java.util.List;
import java.util.UUID;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {
    private final MedicalRecordService service;

    @GetMapping public List<MedicalRecordResponse> all(@RequestParam(required=false) UUID patientId) { return service.getAll(patientId); }
    @GetMapping("/{id}") public MedicalRecordResponse get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public MedicalRecordResponse create(@Valid @RequestBody MedicalRecordRequest request) { return service.create(request); }
    @PutMapping("/{id}") public MedicalRecordResponse update(@PathVariable UUID id, @Valid @RequestBody MedicalRecordRequest request) { return service.update(id, request); }
    @PostMapping(value="/{id}/attachments", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public MedicalRecordResponse attach(@PathVariable UUID id, @RequestPart("file") MultipartFile file) { return service.attach(id, file); }
    @GetMapping("/attachments/{id}") public ResponseEntity<byte[]> download(@PathVariable UUID id) {
        var file = service.getAttachment(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(file.getFileName()).build().toString())
                .body(file.getFileData());
    }
}
