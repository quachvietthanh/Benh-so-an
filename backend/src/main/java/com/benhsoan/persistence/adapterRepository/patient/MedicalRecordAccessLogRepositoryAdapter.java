package com.benhsoan.persistence.adapterRepository.patient;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.benhsoan.domain.patient.MedicalRecordAccessLog;
import com.benhsoan.persistence.entity.patient.MedicalRecordAccessLogEntity;
import com.benhsoan.persistence.jpaRepository.patient.JpaMedicalRecordAccessLogRepository;
import com.benhsoan.persistence.mapper.patient.MedicalRecordAccessLogPersistenceMapper;
import com.benhsoan.port.outbound.repository.logRepository.MedicalRecordAccessLogRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MedicalRecordAccessLogRepositoryAdapter
        implements MedicalRecordAccessLogRepository {

    private final JpaMedicalRecordAccessLogRepository jpaRepository;

    private final MedicalRecordAccessLogPersistenceMapper mapper;

    @Override
    public MedicalRecordAccessLog save(
            MedicalRecordAccessLog log
    ) {

        MedicalRecordAccessLogEntity entity = mapper.toEntity(log);

        MedicalRecordAccessLogEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public Optional<MedicalRecordAccessLog> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Page<MedicalRecordAccessLog> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<MedicalRecordAccessLog> findByPatientId(
            UUID patientId,
            Pageable pageable
    ) {

        return jpaRepository.findByPatientId(
                patientId,
                pageable
        ).map(mapper::toDomain);

    }

    @Override
    public Page<MedicalRecordAccessLog> findByVisitId(
            UUID visitId,
            Pageable pageable
    ) {

        return jpaRepository.findByVisitId(
                visitId,
                pageable
        ).map(mapper::toDomain);

    }

}