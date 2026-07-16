package com.benhsoan.port.outbound.authSecurity;

import java.util.Date;
import java.util.List;

public interface JwtProviderPort {
    String generateToken(String username, List<String> roles);
    String getUsernameFromToken(String token);
    List<String> getRolesFromToken(String token);
    boolean validateToken(String token);
    void invalidateToken(String token);
    Date getExpirationDateFromToken(String token);
    boolean isTokenExpiringSoon(String token);
}
