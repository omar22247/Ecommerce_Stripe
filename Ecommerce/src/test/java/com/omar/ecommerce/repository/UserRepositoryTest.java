package com.omar.ecommerce.repository;

import com.omar.ecommerce.entity.User;
import com.omar.ecommerce.entity.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_returnsUser_whenEmailExists() {
        User user = new User();
        user.setEmail("omar1889@example.com");
        user.setRole(Role.ADMIN);
        user.setFirstName("Omar");
        user.setLastName("Abohashim");
        user.setPassword("Test@@1235445");
        user.setEnabled(true);
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("omar1889@example.com");
        assertTrue(result.isPresent());
        assertEquals("omar1889@example.com", result.get().getEmail());
    }

    @Test
    void findByEmail_returnsEmpty_whenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("nobody@example.com");
        assertTrue(result.isEmpty());
    }
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
}