package com.benhsoan.persistence.adapterRepository.patient;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.benhsoan.domain.patient.Patient;
import com.benhsoan.persistence.entity.patient.PatientEntity;
import com.benhsoan.persistence.jpaRepository.patient.JpaPatientRepository;
import com.benhsoan.persistence.mapper.patient.PatientPersistenceMapper;
import com.benhsoan.port.outbound.repository.crudRepository.patient.PatientRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepository {

    private final JpaPatientRepository jpaRepository;

    private final PatientPersistenceMapper mapper;

    @Override
    public Page<Patient> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Patient> findByPatientCode(String patientCode) {
        return jpaRepository.findByPatientCode(patientCode)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Patient> findByFullNameContaining(
            String keyword,
            Pageable pageable
    ) {
        return jpaRepository.findByFullNameContainingIgnoreCase(
                keyword,
                pageable
        ).map(mapper::toDomain);
    }

    @Override
    public Patient save(Patient patient) {

        PatientEntity entity = mapper.toEntity(patient);

        PatientEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByPatientCode(String patientCode) {
        return jpaRepository.existsByPatientCode(patientCode);
    }

    @Override
    public boolean existsByIdentityNumber(String identityNumber) {

        return identityNumber != null
                && jpaRepository.existsByIdentityNumber(identityNumber);

    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Patient> findTopByOrderByPatientCodeDesc() {
        return jpaRepository.findTopByOrderByPatientCodeDesc()
            .map(mapper::toDomain);
        }

    @Override
    public boolean existsByIdentityNumberAndIdNot(
        String identityNumber,
        UUID id
    ) {
        return jpaRepository.existsByIdentityNumberAndIdNot(
            identityNumber,
            id
        );
    }

    @Override
    public Optional<Patient> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

}
