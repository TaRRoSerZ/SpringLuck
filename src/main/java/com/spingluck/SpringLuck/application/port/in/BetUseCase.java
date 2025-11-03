package com.spingluck.SpringLuck.application.port.in;

import com.spingluck.SpringLuck.application.domain.model.Bet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BetUseCase {
    void placeBet(Bet bet);
    Optional<List<Bet>> getAllBets();
    Optional<Bet> getBetById(UUID id);
}
