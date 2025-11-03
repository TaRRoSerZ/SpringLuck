package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.adapter.out.pesistence.BetRowMapper;
import com.spingluck.SpringLuck.adapter.out.pesistence.BetSQLAdapter;
import com.spingluck.SpringLuck.application.domain.model.Bet;
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
@Import({BetSQLAdapter.class, BetRowMapper.class})
@Testcontainers
@TestPropertySource(properties = "spring.flyway.enabled=false")
public class BetSQLAdapterTest {

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
        postgre.setPortBindings(Collections.singletonList("6000:5432"));
    }

    @Autowired
    BetSQLAdapter betSQLAdapter;

    @Test
    @Sql({"/db/migrations/V1__createBetTable.sql", "/db/migrations/V2__insertBets.sql"})
    public void getAllBets() {
        Optional<List<Bet>> bets;
        bets = betSQLAdapter.findAll();

        Assertions.assertTrue(bets.isPresent());
        Assertions.assertFalse(bets.get().isEmpty());
        Assertions.assertEquals(2, bets.get().size());

        Bet firstBet = bets.get().getFirst();
        Assertions.assertEquals(100.00, firstBet.getAmount());
        Assertions.assertTrue(firstBet.getIsWinningBet());
    }

    @Test
    @Sql({"/db/migrations/V1__createBetTable.sql", "/db/migrations/V2__insertBets.sql"})
    public void getBetById() {
        Optional<Bet> betId2;
        betId2 = betSQLAdapter.findById(UUID.fromString("250e8400-e29b-41d4-a716-446655440000"));

        if (betId2.isEmpty()) {
            Assertions.fail("Bet with ID 250e8400-e29b-41d4-a716-446655440000 should be present");
        }

        Bet secondBet = betId2.get();

        Assertions.assertEquals(UUID.fromString("250e8400-e29b-41d4-a716-446655440000"), secondBet.getId());
        Assertions.assertEquals(50.00, secondBet.getAmount());
        Assertions.assertFalse(secondBet.getIsWinningBet());
    }

    @Test
    @Sql({"/db/migrations/V1__createBetTable.sql", "/db/migrations/V2__insertBets.sql"})
    public void placeBet() {
        Bet newBet = new Bet(UUID.fromString("650e8400-e29b-41d4-a716-446655440000"),UUID.fromString("750e8400-e29b-41d4-a716-446655440000"), 75.00, new java.util.Date(), true);
        betSQLAdapter.save(newBet);

        Optional<Bet> retrievedBet = betSQLAdapter.findById(UUID.fromString("650e8400-e29b-41d4-a716-446655440000"));

        if (retrievedBet.isEmpty()) {
            Assertions.fail("Newly placed bet should be present");
        }

        Bet placedBet = retrievedBet.get();

        Assertions.assertEquals(UUID.fromString("650e8400-e29b-41d4-a716-446655440000"), placedBet.getId());
        Assertions.assertEquals(75.00, placedBet.getAmount());
        Assertions.assertTrue(placedBet.getIsWinningBet());
    }
}
