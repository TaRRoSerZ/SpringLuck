package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.adapter.out.pesistence.TransactionRowMapper;
import com.spingluck.SpringLuck.adapter.out.pesistence.TransactionSQLAdapter;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TransactionSQLAdapter.class, TransactionRowMapper.class})
@Testcontainers
@TestPropertySource(properties = "spring.flyway.enabled=false")
public class TransactionSQLAdapterTest {

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
    TransactionSQLAdapter transactionSQLAdapter;

    @Test
    @Sql({"/db/migrations/V3__createTransactionTable.sql", "/db/migrations/V4__insertTransactions.sql"})
    public void getAllTransactions() {
        Optional<List<Transaction>> transactions;
        transactions = transactionSQLAdapter.findAll();
        Assertions.assertTrue(transactions.isPresent());
        Assertions.assertFalse(transactions.get().isEmpty());
        Assertions.assertEquals(5, transactions.get().size());

        Transaction firstTransaction = transactions.get().getFirst();
        Assertions.assertEquals(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"), firstTransaction.getId());
        Assertions.assertEquals(100.0, firstTransaction.getAmount());
        Assertions.assertNull(firstTransaction.getBetId());
        Assertions.assertEquals("DEPOSIT", firstTransaction.getType().name());
    }
}
