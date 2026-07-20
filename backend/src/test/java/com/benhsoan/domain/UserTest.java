package com.benhsoan.domain;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import com.benhsoan.domain.auth.User;

class UserTest {

    @Test
    void shouldDeactivateUser() {

        User user = User.create(
                "admin",
                "123456",
                "Administrator",
                "admin@gmail.com",
                "0123456789",
                UUID.randomUUID()
        );

        user.deactivate();

        assertFalse(
                user.isActive()
        );
    }
}
