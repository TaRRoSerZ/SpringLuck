package com.spingluck.SpringLuck.adapter.out.pesistence;

import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TransactionRowMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Transaction(UUID.fromString(rs.getString("id")),
                        rs.getDouble("amount"),
                        UUID.fromString(rs.getString("bet_id")),
                TransactionType.valueOf(rs.getString("type")),
                rs.getDate("date"));
    }
}
