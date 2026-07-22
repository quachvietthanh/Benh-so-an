package com.benhsoan.persistence.mapper.appointment;

import org.springframework.stereotype.Component;

import com.benhsoan.domain.appointment.AppointmentReminder;
import com.benhsoan.persistence.entity.appointment.AppointmentReminderEntity;

@Component
public class AppointmentReminderPersistenceMapper {

    public AppointmentReminder toDomain(AppointmentReminderEntity entity) {
        if (entity == null) {
            return null;
        }

        return AppointmentReminder.restore(
                entity.getId(),
                entity.getAppointmentId(),
                entity.getChannel(),
                entity.getRemindAt(),
                entity.getStatus(),
                entity.getSentAt(),
                entity.getCreatedAt()
        );
    }

    public AppointmentReminderEntity toEntity(AppointmentReminder domain) {
        if (domain == null) {
            return null;
        }

        return AppointmentReminderEntity.builder()
                .id(domain.getId())
                .appointmentId(domain.getAppointmentId())
                .channel(domain.getChannel())
                .remindAt(domain.getRemindAt())
                .status(domain.getStatus())
                .sentAt(domain.getSentAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }

}