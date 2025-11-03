package com.spingluck.SpringLuck.adapter.out.pesistence;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BetRowMapper implements RowMapper<Bet> {
    @Override
    public Bet mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Bet(UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("user_id")),
                rs.getDouble("amount"),
                rs.getDate("date"),
                rs.getBoolean("isWinningBet"));
    }
}
