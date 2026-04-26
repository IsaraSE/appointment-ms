package com.ctse.patient_service.repository;

import com.ctse.patient_service.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataMongoTest
class UserRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void setUpAll() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void tearDownAll() {
        mongoDBContainer.stop();
    }

    @Test
    void testSaveAndFindUserByEmail() {
        User user = new User();
        user.setPatientName("testpatient");
        user.setContactNumber("1234567890");
        user.setAge("30");
        user.setPasswordHash("password123");
        user.setEmail("test@email.com");
        user.setRole("PATIENT");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test@email.com");

        assertTrue(foundUser.isPresent());
        assertEquals("testpatient", foundUser.get().getPatientName());
        assertEquals("PATIENT", foundUser.get().getRole());
    }
}
