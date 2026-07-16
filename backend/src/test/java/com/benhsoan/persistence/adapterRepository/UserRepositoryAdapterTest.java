package com.benhsoan.persistence.adapterRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.benhsoan.domain.auth.Role;
import com.benhsoan.domain.auth.User;
import com.benhsoan.port.outbound.repository.RoleRepository;
import com.benhsoan.port.outbound.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class UserRepositoryAdapterTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;

    @BeforeEach
    void setUp() {

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        user = User.create(
                "admin",
                "123456",
                "Administrator",
                "admin@gmail.com",
                "0123456789",
                adminRole.getId()
        );

        userRepository.save(user);
    }

    @Test
    void shouldSaveAndFindUserByUsername() {

        Optional<User> result = userRepository.findByUsername("admin");

        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsername());
    }

    @Test
    void shouldFindUserById() {

        Optional<User> result = userRepository.findById(user.getId());

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    void shouldExistsUsername() {

        assertTrue(userRepository.existsByUsername("admin"));
    }

    @Test
    void shouldExistsEmail() {

        assertTrue(userRepository.existsByEmail("admin@gmail.com"));
    }

    @Test
    void shouldDeleteUser() {

        userRepository.deleteById(user.getId());

        assertFalse(
                userRepository.findById(user.getId()).isPresent()
        );
    }
}