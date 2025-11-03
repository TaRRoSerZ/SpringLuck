package com.spingluck.SpringLuck.adapter.out.pesistence;

import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.port.out.TransactionPort;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class TransactionSQLAdapter implements TransactionPort {

    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Optional<Transaction> save(Transaction transaction) {
        String sqlQuery = "INSERT INTO transactions (amount, betId, type, date) VALUES (?, ?, ?, ?);";
        jdbcTemplate.update(sqlQuery, transaction.getAmount(), transaction.getBetId(), transaction.getType(), transaction.getDate());
        return Optional.of(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(int id) {
        String sqlQuery = "SELECT * FROM transactions WHERE id = ?;";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new TransactionRowMapper(), id));

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Transaction>> findAll() {
        String sqlQuery = "SELECT * FROM transactions;";
        return Optional.of(jdbcTemplate.query(sqlQuery, new TransactionRowMapper()));
    }
}
