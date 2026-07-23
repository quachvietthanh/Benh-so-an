package com.benhsoan.appointment;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
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
    private static final ZoneId CLINIC_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    static final Duration NO_SHOW_GRACE_PERIOD = Duration.ofMinutes(15);

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
                .appointmentCode(newAppointmentCode())
                .patientId(request.patientId()).doctorId(request.doctorId())
                .department(request.department().trim()).appointmentAt(request.appointmentAt())
                .status(AppointmentStatus.SCHEDULED).reason(request.reason())
                .createdAt(now).updatedAt(now).build();
        return toResponse(repository.save(entity));
    }

    static String newAppointmentCode() {
        String randomPart = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase(Locale.ROOT);
        return "LH-" + randomPart;
    }

    public AppointmentResponse cancel(UUID id, String reason) {
        AppointmentEntity item = repository.findByIdForStatusChange(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn"));
        if (item.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lịch hẹn đã được hủy trước đó");
        }
        if (item.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chỉ có thể hủy lịch đang ở trạng thái đã đặt");
        }

        Instant now = Instant.now();
        if (!item.getAppointmentAt().isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lịch hẹn đã đến hoặc quá giờ nên không thể hủy");
        }

        String normalizedReason = reason == null ? "" : reason.trim();
        if (normalizedReason.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập lý do hủy");
        }
        if (normalizedReason.length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lý do hủy không được vượt quá 500 ký tự");
        }

        item.setStatus(AppointmentStatus.CANCELLED);
        item.setCancelReason(normalizedReason);
        item.setUpdatedAt(now);
        return toResponse(repository.save(item));
    }

    public AppointmentResponse noShow(UUID id) {
        AppointmentEntity item = repository.findByIdForStatusChange(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn"));
        if (item.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ có thể đánh dấu không đến cho lịch đang ở trạng thái đã đặt"
            );
        }

        Instant now = Instant.now();
        if (!isNoShowEligible(item.getAppointmentAt(), now)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ có thể đánh dấu không đến từ 15 phút sau giờ hẹn"
            );
        }
        item.setStatus(AppointmentStatus.NO_SHOW);
        item.setUpdatedAt(now);
        return toResponse(repository.save(item));
    }

    static boolean isNoShowEligible(Instant appointmentAt, Instant now) {
        return !now.isBefore(appointmentAt.plus(NO_SHOW_GRACE_PERIOD));
    }

    public AppointmentResponse checkIn(UUID id) {
        AppointmentEntity item = requireScheduled(id);
        LocalDate appointmentDate = item.getAppointmentAt().atZone(CLINIC_ZONE).toLocalDate();
        if (!appointmentDate.equals(LocalDate.now(CLINIC_ZONE))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ được check-in trong ngày hẹn");
        }
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
        AppointmentEntity item = repository.findByIdForStatusChange(id)
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
        AppointmentEntity item = repository.findByIdForStatusChange(id)
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
