package com.benhsoan.appointment;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.benhsoan.persistence.jpaRepository.auth.JpaUserRepository;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService service;
    private final JpaUserRepository userRepository;

    public record DoctorOption(UUID id, String fullName) {}

    @GetMapping public List<AppointmentResponse> getAll() { return service.getAll(); }
    @GetMapping("/doctors") public List<DoctorOption> doctors() {
        return userRepository.findActiveDoctors().stream()
                .map(user -> new DoctorOption(user.getId(), user.getFullName())).toList();
    }
    @PostMapping public AppointmentResponse create(@Valid @RequestBody AppointmentRequest request) { return service.create(request); }
    @GetMapping("/queue") public List<AppointmentResponse> queue() { return service.getQueue(); }
    @PostMapping("/queue/call-next") public AppointmentResponse callNext() { return service.callNext(); }
    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(
            @PathVariable UUID id,
            @Valid @RequestBody CancelAppointmentRequest request
    ) {
        return service.cancel(id, request.reason());
    }
    @PatchMapping("/{id}/no-show") public AppointmentResponse noShow(@PathVariable UUID id) { return service.noShow(id); }
    @PatchMapping("/{id}/check-in") public AppointmentResponse checkIn(@PathVariable UUID id) { return service.checkIn(id); }
    @PatchMapping("/{id}/complete") public AppointmentResponse complete(@PathVariable UUID id) { return service.complete(id); }
}
