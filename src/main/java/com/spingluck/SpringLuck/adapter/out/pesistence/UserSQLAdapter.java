package com.spingluck.SpringLuck.adapter.out.pesistence;

import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.User;
import com.spingluck.SpringLuck.application.port.in.TransactionUseCase;
import com.spingluck.SpringLuck.application.port.out.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserSQLAdapter implements UserPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> saveUser(User user) {
        String sql = "INSERT INTO users (id, email, balance, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        int rows = jdbcTemplate.update(sql,
                user.getId(),
                user.getEmail(),
                user.getBalance(),
                user.isActive(),
                Timestamp.from(user.getCreatedAt()),
                Timestamp.from(user.getUpdatedAt())
        );
        return rows > 0 ? Optional.of(user) : Optional.empty();
    }

    @Override
    public Optional<List<User>> findAllUsers() {
        String sql = "SELECT * FROM users";
        return Optional.of(jdbcTemplate.query(sql, new UserRowMapper()));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);
            return Optional.ofNullable(user);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateBalance(String email, Double amount) {
        String updateSql = "UPDATE users SET balance = balance + ?, updated_at = ? WHERE email = ?";
        jdbcTemplate.update(updateSql, amount, Timestamp.from(Instant.now()), email);
    }
}
