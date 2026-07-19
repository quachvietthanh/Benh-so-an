package com.benhsoan.port.outbound.repository.logRepository;

import java.util.List;
import java.util.UUID;

import com.benhsoan.domain.auth.LoginLog;
import com.benhsoan.port.outbound.repository.BaseRepository;

public interface LoginLogRepository
        extends BaseRepository<LoginLog, UUID> {

    List<LoginLog> findByUserId(UUID userId);
}