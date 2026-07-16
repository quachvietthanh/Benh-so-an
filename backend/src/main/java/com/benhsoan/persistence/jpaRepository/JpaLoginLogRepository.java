package com.benhsoan.persistence.jpaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benhsoan.persistence.entity.LoginLogEntity;

public interface JpaLoginLogRepository
        extends JpaRepository<LoginLogEntity, UUID> {

    List<LoginLogEntity> findByUserId(UUID userId);
}