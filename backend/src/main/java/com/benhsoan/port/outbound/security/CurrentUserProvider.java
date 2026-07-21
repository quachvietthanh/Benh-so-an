package com.benhsoan.port.outbound.security;

import java.util.UUID;

public interface CurrentUserProvider {

    UUID getCurrentUserId();

}