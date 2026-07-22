package com.benhsoan.appointment;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.benhsoan.persistence.entity.auth.UserEntity;
import com.benhsoan.persistence.entity.patient.PatientEntity;
import com.benhsoan.persistence.jpaRepository.auth.JpaUserRepository;
import com.benhsoan.persistence.jpaRepository.patient.JpaPatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {
    private final AppointmentRepository repository;
    private final JpaPatientRepository patientRepository;
    private final JpaUserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAll() {
        return repository.findAllByOrderByAppointmentAtDesc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getQueue() {
        return repository.findByStatusOrderByCheckedInAtAsc(AppointmentStatus.CHECKED_IN)
                .stream().map(this::toResponse).toList();
    }

    public AppointmentResponse create(AppointmentRequest request) {
        patientRepository.findById(request.patientId())
                .filter(PatientEntity::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hồ sơ bệnh nhân không tồn tại hoặc đã ngừng hoạt động"));
        userRepository.findById(request.doctorId())
                .filter(UserEntity::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bác sĩ không tồn tại hoặc đã ngừng hoạt động"));
        Instant from = request.appointmentAt().minus(Duration.ofMinutes(29));
        Instant to = request.appointmentAt().plus(Duration.ofMinutes(29));
        if (repository.existsByDoctorIdAndAppointmentAtBetweenAndStatusNot(
                request.doctorId(), from, to, AppointmentStatus.CANCELLED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bác sĩ đã có lịch trong khung giờ này");
        }
        Instant now = Instant.now();
        AppointmentEntity entity = AppointmentEntity.builder()
                .id(UUID.randomUUID())
                .appointmentCode("LH-" + now.toEpochMilli())
                .patientId(request.patientId()).doctorId(request.doctorId())
                .department(request.department().trim()).appointmentAt(request.appointmentAt())
                .status(AppointmentStatus.SCHEDULED).reason(request.reason())
                .createdAt(now).updatedAt(now).build();
        return toResponse(repository.save(entity));
    }

    public AppointmentResponse cancel(UUID id, String reason) {
        AppointmentEntity item = requireScheduled(id);
        if (reason == null || reason.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải nhập lý do hủy");
        item.setStatus(AppointmentStatus.CANCELLED);
        item.setCancelReason(reason.trim());
        item.setUpdatedAt(Instant.now());
        return toResponse(repository.save(item));
    }

    public AppointmentResponse noShow(UUID id) {
        AppointmentEntity item = requireScheduled(id);
        if (item.getAppointmentAt().plus(Duration.ofMinutes(15)).isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ đánh dấu không đến sau giờ hẹn 15 phút");
        }
        item.setStatus(AppointmentStatus.NO_SHOW);
        item.setUpdatedAt(Instant.now());
        return toResponse(repository.save(item));
    }

    public AppointmentResponse checkIn(UUID id) {
        AppointmentEntity item = requireScheduled(id);
        item.setStatus(AppointmentStatus.CHECKED_IN);
        item.setCheckedInAt(Instant.now());
        item.setUpdatedAt(Instant.now());
        return toResponse(repository.save(item));
    }

    public AppointmentResponse callNext() {
        AppointmentEntity item = repository.findByStatusOrderByCheckedInAtAsc(AppointmentStatus.CHECKED_IN)
                .stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hàng đợi đang trống"));
        item.setStatus(AppointmentStatus.CALLED);
        item.setUpdatedAt(Instant.now());
        return toResponse(repository.save(item));
    }

    public AppointmentResponse complete(UUID id) {
        AppointmentEntity item = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn"));
        if (item.getStatus() != AppointmentStatus.CALLED) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bệnh nhân chưa được gọi khám");
        item.setStatus(AppointmentStatus.COMPLETED);
        item.setUpdatedAt(Instant.now());
        return toResponse(repository.save(item));
    }

    @Scheduled(fixedDelay = 300000)
    public void markDueReminders() {
        Instant now = Instant.now();
        List<AppointmentEntity> due = repository.findByStatusAndReminderSentAtIsNullAndAppointmentAtBetween(
                AppointmentStatus.SCHEDULED, now, now.plus(Duration.ofHours(24)));
        due.forEach(item -> { item.setReminderSentAt(now); item.setUpdatedAt(now); });
        repository.saveAll(due);
    }

    private AppointmentEntity requireScheduled(UUID id) {
        AppointmentEntity item = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn"));
        if (item.getStatus() != AppointmentStatus.SCHEDULED) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lịch hẹn không còn ở trạng thái đã đặt");
        return item;
    }

    private AppointmentResponse toResponse(AppointmentEntity item) {
        String patientName = patientRepository.findById(item.getPatientId()).map(PatientEntity::getFullName).orElse("---");
        String doctorName = userRepository.findById(item.getDoctorId()).map(UserEntity::getFullName).orElse("---");
        return new AppointmentResponse(item.getId(), item.getAppointmentCode(), item.getPatientId(), patientName,
                item.getDoctorId(), doctorName, item.getDepartment(), item.getAppointmentAt(), item.getStatus(),
                item.getReason(), item.getCancelReason(), item.getCheckedInAt(), item.getReminderSentAt());
    }
}
