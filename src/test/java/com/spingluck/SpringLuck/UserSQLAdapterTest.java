package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.adapter.out.pesistence.UserRowMapper;
import com.spingluck.SpringLuck.adapter.out.pesistence.UserSQLAdapter;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserSQLAdapter.class, UserRowMapper.class})
@Testcontainers
@TestPropertySource(properties = "spring.flyway.enabled=false")
public class UserSQLAdapterTest {

    @Container
    static PostgreSQLContainer<?> postgre = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void datasourceConfig(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgre::getJdbcUrl);
        registry.add("spring.datasource.username", postgre::getUsername);
        registry.add("spring.datasource.password", postgre::getPassword);
    }

    static {
        postgre.setPortBindings(Collections.singletonList("5566:5432"));
    }

    @Autowired
    UserSQLAdapter userSQLAdapter;

    @Test
    @Sql({"/db/migrations/V5__createUserTable.sql", "/db/migrations/V6__insertUsers.sql"})
    public void getAllUsers() {
        Optional<List<User>> users;
        users = userSQLAdapter.findAllUsers();
        Assertions.assertTrue(users.isPresent());
        Assertions.assertFalse(users.get().isEmpty());
        Assertions.assertEquals(5, users.get().size());

        User firstUser = users.get().getFirst();
        Assertions.assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), firstUser.getId());
        Assertions.assertEquals(150.50, firstUser.getBalance());
        Assertions.assertTrue(firstUser.isActive());
        Assertions.assertEquals("alice@example.com", firstUser.getEmail());
    }

    @Test
    @Sql({"/db/migrations/V5__createUserTable.sql", "/db/migrations/V6__insertUsers.sql"})
    public void getUserByEmail() {
        Optional<User> user;
        user = userSQLAdapter.findUserByEmail("alice@example.com");

        if (user.isEmpty()) {
            Assertions.fail("No user found");
        }

        User user2 = user.get();

        Assertions.assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), user2.getId());
        Assertions.assertEquals(150.50, user2.getBalance());
        Assertions.assertTrue(user2.isActive());
        Assertions.assertEquals("alice@example.com", user2.getEmail());
    }

    @Test
    @Sql({"/db/migrations/V5__createUserTable.sql", "/db/migrations/V6__insertUsers.sql"})
    public void saveUser() {
        User user = new User(UUID.randomUUID(), "jean@gmail.com", 150.50, true, Instant.now(), Instant.now());
        userSQLAdapter.saveUser(user);

        Optional<User> retrievedUser = userSQLAdapter.findUserByEmail("jean@gmail.com");

        if (retrievedUser.isEmpty()) {
            Assertions.fail("No user found");
        }

        User savedUser = retrievedUser.get();

        Assertions.assertEquals(user.getId(), savedUser.getId());
        Assertions.assertEquals(150.50, savedUser.getBalance());
        Assertions.assertTrue(savedUser.isActive());
        Assertions.assertEquals("jean@gmail.com", savedUser.getEmail());
    }

    @Test
    @Sql({"/db/migrations/V5__createUserTable.sql", "/db/migrations/V6__insertUsers.sql"})
    public void makeTransaction(){
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Optional<User> userOpt = userSQLAdapter.findUserByEmail("alice@example.com");

        Assertions.assertTrue(userOpt.isPresent(), "User should exist before update");

        User user = userOpt.get();
        double oldBalance = user.getBalance();

        double delta = 50.0;
        userSQLAdapter.updateBalance("alice@example.com", delta);

        Optional<User> updatedUserOpt = userSQLAdapter.findUserByEmail("alice@example.com");
        Assertions.assertTrue(updatedUserOpt.isPresent(), "User should exist after update");

        User updatedUser = updatedUserOpt.get();
        Assertions.assertEquals(oldBalance + delta, updatedUser.getBalance(), 0.001, "Balance should be increased");
    }
}
