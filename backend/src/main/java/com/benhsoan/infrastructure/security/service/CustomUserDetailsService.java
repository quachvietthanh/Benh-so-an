package com.benhsoan.infrastructure.security.service;

import com.benhsoan.domain.auth.User;
import com.benhsoan.port.outbound.repository.crudRepository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Login failed: user '{}' not found", username);
                    return new UsernameNotFoundException("Tài khoản không tồn tại");
                });

        if (user.isLocked()) {
            log.warn("Login blocked: user '{}' is locked", username);
            throw new LockedException("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.");
        }

        if (!user.isActive()) {
            log.warn("Login blocked: user '{}' is deactivated", username);
            throw new LockedException("Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.");
        }

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                true,
                true,
                true,
                !user.isLocked(),
                authorities
        );
    }
}
