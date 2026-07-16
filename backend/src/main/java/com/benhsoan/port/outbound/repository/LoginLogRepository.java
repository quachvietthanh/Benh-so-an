package com.benhsoan.port.outbound.repository;

import java.util.List;
import java.util.UUID;

import com.benhsoan.domain.auth.LoginLog;

public interface LoginLogRepository
        extends BaseRepository<LoginLog, UUID> {

    List<LoginLog> findByUserId(UUID userId);
}