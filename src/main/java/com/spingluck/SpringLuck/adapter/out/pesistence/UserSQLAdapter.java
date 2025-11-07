package com.spingluck.SpringLuck.adapter.out.pesistence;

import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.User;
import com.spingluck.SpringLuck.application.port.out.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.security.Timestamp;
import java.sql.Date;
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
                user.getCreatedAt(),
                user.getUpdatedAt()
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
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new UserRowMapper(), email));
    }

    @Override
    public void makeTransaction(Transaction transaction, User user) {
        String insertSql  = "INSERT INTO transactions (id, amount, bet_id, user_id, type, date) VALUES (?, ?, ?, ?, ?::transaction_type, ?)";
        jdbcTemplate.update(insertSql ,
                transaction.getId(),
                transaction.getAmount(),
                transaction.getBetId(),
                transaction.getUserId(),
                transaction.getType().name(),
                transaction.getDate()
        );

        String updateSql = "UPDATE users SET balance = balance + ? WHERE id = ?";
        jdbcTemplate.update(updateSql, transaction.getAmount(), user.getId());
    }
}
