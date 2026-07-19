package com.benhsoan.persistence.mapper.patient;

import org.springframework.stereotype.Component;

import com.benhsoan.domain.patient.Visit;
import com.benhsoan.persistence.entity.patient.VisitEntity;

@Component
public class VisitPersistenceMapper {

    public Visit toDomain(VisitEntity entity) {

        if (entity == null) {
            return null;
        }

        return Visit.restore(
                entity.getId(),
                entity.getPatientId(),
                entity.getDoctorId(),
                entity.getDepartmentId(),
                entity.getVisitCode(),
                entity.getVisitType(),
                entity.getVisitStatus(),
                entity.getVisitAt(),
                entity.getReason(),
                entity.getNote(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public VisitEntity toEntity(Visit domain) {

        if (domain == null) {
            return null;
        }

        return VisitEntity.builder()
                .id(domain.getId())
                .patientId(domain.getPatientId())
                .doctorId(domain.getDoctorId())
                .departmentId(domain.getDepartmentId())
                .visitCode(domain.getVisitCode())
                .visitType(domain.getVisitType())
                .visitStatus(domain.getVisitStatus())
                .visitAt(domain.getVisitAt())
                .reason(domain.getReason())
                .note(domain.getNote())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

}