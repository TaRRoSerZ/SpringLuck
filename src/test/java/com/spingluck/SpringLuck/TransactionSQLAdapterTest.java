package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.adapter.out.pesistence.TransactionRowMapper;
import com.spingluck.SpringLuck.adapter.out.pesistence.TransactionSQLAdapter;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionStatus;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
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
import org.springframework.transaction.jta.JtaTransactionManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.*;

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
    @Sql({"/db/migrations/V3__createTransactionTable.sql", "/db/migrations/V4__insertTransactions.sql", "/db/migrations/V7__alterTableTransactions.sql"})
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

    @Test
    @Sql({"/db/migrations/V3__createTransactionTable.sql", "/db/migrations/V4__insertTransactions.sql", "/db/migrations/V7__alterTableTransactions.sql"})
    public void getAllUserTransactions() {
        Optional<List<Transaction>> transactions;
        transactions = transactionSQLAdapter.findTransactionsByUserId(UUID.fromString("380e8400-e29b-41d4-a716-446655440000"));
        Assertions.assertTrue(transactions.isPresent());
        Assertions.assertFalse(transactions.get().isEmpty());
        Assertions.assertEquals(1, transactions.get().size());

        Transaction firstTransaction = transactions.get().getFirst();
        Assertions.assertEquals(UUID.fromString("360e8400-e29b-41d4-a716-446655440000"), firstTransaction.getId());
        Assertions.assertEquals(20.0, firstTransaction.getAmount());
        Assertions.assertEquals(firstTransaction.getBetId(), UUID.fromString("370e8400-e29b-41d4-a716-446655440000"));
        Assertions.assertEquals("BET_PLACED", firstTransaction.getType().name());
    }

    @Test
    @Sql({"/db/migrations/V3__createTransactionTable.sql", "/db/migrations/V4__insertTransactions.sql", "/db/migrations/V7__alterTableTransactions.sql"})
    public void getTransactionById() {
        Optional<Transaction> transactionId2;
        transactionId2 = transactionSQLAdapter.findById(UUID.fromString("260e8400-e29b-41d4-a716-446655440000"));

        if (transactionId2.isEmpty()) {
            Assertions.fail("No transaction found");
        }

        Transaction transaction2 = transactionId2.get();

        Assertions.assertEquals(UUID.fromString("260e8400-e29b-41d4-a716-446655440000"), transaction2.getId());
        Assertions.assertEquals(50.0, transaction2.getAmount());
        Assertions.assertNull(transaction2.getBetId());
        Assertions.assertEquals("WITHDRAWAL", transaction2.getType().name());
    }

    @Test
    @Sql({"/db/migrations/V3__createTransactionTable.sql", "/db/migrations/V4__insertTransactions.sql", "/db/migrations/V7__alterTableTransactions.sql"})
    public void placeTransaction() {
        Transaction newTransaction = new Transaction(UUID.fromString("900e8400-e29b-41d4-a716-446655440000"), 500.0, null,
                UUID.fromString("270e8400-e29b-41d4-a716-446655440000"),"str_2", TransactionType.DEPOSIT, TransactionStatus.CONFIRMED, new Date());
        transactionSQLAdapter.save(newTransaction);

        Optional<Transaction> retrievedTransaction = transactionSQLAdapter.findById(UUID.fromString("900e8400-e29b-41d4-a716-446655440000"));

        if (retrievedTransaction.isEmpty()) {
            Assertions.fail("Newly placed transaction should be present");
        }

        Transaction madeTransaction = retrievedTransaction.get();

        Assertions.assertEquals(UUID.fromString("900e8400-e29b-41d4-a716-446655440000"), madeTransaction.getId());
        Assertions.assertEquals(500.0, madeTransaction.getAmount());
        Assertions.assertNull(madeTransaction.getBetId());
    }

}
