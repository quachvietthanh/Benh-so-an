package com.benhsoan.persistence.adapterRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.benhsoan.domain.auth.User;
import com.benhsoan.port.outbound.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class UserRepositoryAdapterTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {

        User user = User.create(
                "admin",
                "123456",
                "Administrator",
                "admin@gmail.com",
                "0123456789",
                UUID.randomUUID()
        );

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByUsername("admin");

        assertTrue(result.isPresent());

        assertEquals(
                "admin",
                result.get().getUsername()
        );
    }
}
