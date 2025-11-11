package com.spingluck.SpringLuck.adapter.out.pesistence;

import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionStatus;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TransactionRowMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        System.out.println("bet_id value = " + rs.getString("bet_id"));
        System.out.println("user_id value = " + rs.getString("user_id"));

        return new Transaction(
                UUID.fromString(rs.getString("id")),
                rs.getDouble("amount"),
                rs.getString("bet_id") == null ? null : UUID.fromString(rs.getString("bet_id")),
                UUID.fromString(rs.getString("user_id")),
                rs.getString("stripe_intent_id") == null ? null : rs.getString("stripe_intent_id"),
                TransactionType.valueOf(rs.getString("type")),
                TransactionStatus.valueOf(rs.getString("status")),
                rs.getDate("date"));
    }
}
