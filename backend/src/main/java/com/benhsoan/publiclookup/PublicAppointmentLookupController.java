package com.benhsoan.publiclookup;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public/appointments")
@RequiredArgsConstructor
public class PublicAppointmentLookupController {
    private final PublicAppointmentLookupService lookupService;

    @PostMapping("/lookup")
    public ResponseEntity<PublicAppointmentLookupResponse> lookup(
            @Valid @RequestBody PublicAppointmentLookupRequest request) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(lookupService.lookup(request));
    }
}
