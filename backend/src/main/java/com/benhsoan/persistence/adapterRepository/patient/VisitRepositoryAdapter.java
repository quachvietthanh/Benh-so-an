package com.benhsoan.persistence.adapterRepository.patient;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.benhsoan.domain.patient.Visit;
import com.benhsoan.persistence.entity.patient.VisitEntity;
import com.benhsoan.persistence.jpaRepository.patient.JpaVisitRepository;
import com.benhsoan.persistence.mapper.patient.VisitPersistenceMapper;
import com.benhsoan.port.outbound.repository.crudRepository.patient.VisitRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VisitRepositoryAdapter implements VisitRepository {

    private final JpaVisitRepository jpaRepository;

    private final VisitPersistenceMapper mapper;

    @Override
    public Page<Visit> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Visit> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Visit> findByVisitCode(String visitCode) {
        return jpaRepository.findByVisitCode(visitCode)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Visit> findByPatientId(
            UUID patientId,
            Pageable pageable
    ) {

        return jpaRepository.findByPatientId(
                patientId,
                pageable
        ).map(mapper::toDomain);

    }

    @Override
    public Page<Visit> findByDoctorId(
            UUID doctorId,
            Pageable pageable
    ) {

        return jpaRepository.findByDoctorId(
                doctorId,
                pageable
        ).map(mapper::toDomain);

    }

    @Override
    public Visit save(Visit visit) {

        VisitEntity entity = mapper.toEntity(visit);

        VisitEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);

    }

    @Override
    public boolean existsByVisitCode(String visitCode) {
        return jpaRepository.existsByVisitCode(visitCode);
    }

    @Override
    public boolean existsByPatientIdAndDoctorId(
            UUID patientId,
            UUID doctorId
    ) {
        return jpaRepository.existsByPatientIdAndDoctorId(
                patientId,
                doctorId
        );
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Visit> findByPatientIdWithDateFilter(
            UUID patientId,
            Instant fromDate,
            Instant toDate,
            Pageable pageable
    ) {

        Specification<VisitEntity> spec = (root, query, cb) -> {
            Predicate patientPredicate =
                    cb.equal(root.get("patientId"), patientId);

            if (fromDate != null && toDate != null) {
                Predicate datePredicate = cb.between(
                        root.get("visitAt"), fromDate, toDate
                );
                return cb.and(patientPredicate, datePredicate);
            }

            if (fromDate != null) {
                Predicate datePredicate =
                        cb.greaterThanOrEqualTo(
                                root.get("visitAt"), fromDate
                        );
                return cb.and(patientPredicate, datePredicate);
            }

            if (toDate != null) {
                Predicate datePredicate =
                        cb.lessThanOrEqualTo(
                                root.get("visitAt"), toDate
                        );
                return cb.and(patientPredicate, datePredicate);
            }

            return patientPredicate;
        };

        return jpaRepository.findAll(spec, pageable)
                .map(mapper::toDomain);
    }

}
