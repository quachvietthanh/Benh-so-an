package com.benhsoan.persistence.mapper.appointment;

import org.springframework.stereotype.Component;

import com.benhsoan.domain.appointment.AppointmentQueue;
import com.benhsoan.persistence.entity.appointment.AppointmentQueueEntity;

@Component
public class AppointmentQueuePersistenceMapper {

    public AppointmentQueue toDomain(AppointmentQueueEntity entity) {
        if (entity == null) {
            return null;
        }

        return AppointmentQueue.restore(
                entity.getId(),
                entity.getAppointmentId(),
                entity.getQueueNumber(),
                entity.getStatus(),
                entity.getCheckedInAt(),
                entity.getCalledAt(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getCreatedAt()
        );
    }

    public AppointmentQueueEntity toEntity(AppointmentQueue domain) {
        if (domain == null) {
            return null;
        }

        return AppointmentQueueEntity.builder()
                .id(domain.getId())
                .appointmentId(domain.getAppointmentId())
                .queueNumber(domain.getQueueNumber())
                .status(domain.getStatus())
                .checkedInAt(domain.getCheckedInAt())
                .calledAt(domain.getCalledAt())
                .startedAt(domain.getStartedAt())
                .completedAt(domain.getCompletedAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }

}