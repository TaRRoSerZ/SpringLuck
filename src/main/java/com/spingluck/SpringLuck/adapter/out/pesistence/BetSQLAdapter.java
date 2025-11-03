package com.spingluck.SpringLuck.adapter.out.pesistence;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.port.out.BetPort;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class BetSQLAdapter implements BetPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(Bet bet) {
        String sqlQuery = "INSERT INTO bets (amount, date, isWinningBet) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, bet.getAmount(), bet.getDate(), bet.getIsWinningBet());
    }

    @Override
    public Optional<Bet> findById(UUID id) {
        String sqlQuery = "SELECT * FROM bets WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new BetRowMapper(), id));
    }

    @Override
    public Optional<List<Bet>> findAll() {
        String sqlQuery = "SELECT * FROM bets";
        return Optional.of(jdbcTemplate.query(sqlQuery, new BetRowMapper()));
    }
}
