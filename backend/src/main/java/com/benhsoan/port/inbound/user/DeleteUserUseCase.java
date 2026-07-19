package com.benhsoan.port.inbound.user;

import java.util.UUID;

public interface DeleteUserUseCase {

    void delete(UUID id);

}