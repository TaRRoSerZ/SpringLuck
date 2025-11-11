package com.spingluck.SpringLuck.adapter.out.pesistence;

import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionStatus;
import com.spingluck.SpringLuck.application.port.out.TransactionPort;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class TransactionSQLAdapter implements TransactionPort {

    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Optional<Transaction> save(Transaction t) {
        String sql = """
        
                INSERT INTO transactions (id, amount, bet_id, user_id, stripe_intent_id, type, status, date)
        VALUES (?, ?, ?, ?, ?, ?::transaction_type, ?::transaction_status, ?)
        """;

        jdbcTemplate.update(
                sql,
                t.getId(),
                t.getAmount(),
                t.getBetId(),
                t.getUserId(),
                t.getStripeIntentId(),
                t.getType().name(),
                t.getStatus().name(),
                new java.sql.Timestamp(t.getDate().getTime())
        );
        return Optional.of(t);
    }

    @Override
    public Optional<List<Transaction>> findTransactionsByUserId(UUID userId) {
        String sqlQuery = "SELECT * FROM transactions WHERE user_id = ?";
        return Optional.of(jdbcTemplate.query(sqlQuery, new TransactionRowMapper(), userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> findById(UUID id) {
        String sqlQuery = "SELECT * FROM transactions WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, new TransactionRowMapper(), id));

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Transaction>> findAll() {
        String sqlQuery = "SELECT * FROM transactions";
        return Optional.of(jdbcTemplate.query(sqlQuery, new TransactionRowMapper()));
    }

    @Override
    public Optional<Transaction> findByStripeIntentId(String intentId) {
        String sqlQuery = "SELECT * FROM transactions WHERE stripe_intent_id = ?";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sqlQuery, new TransactionRowMapper(), intentId)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateStatusByStripeId(String intentId, TransactionStatus status) {
        String sql = "UPDATE transactions SET status = ?::transaction_status WHERE stripe_intent_id = ?";
        jdbcTemplate.update(sql, status.name(), intentId);
    }

}
