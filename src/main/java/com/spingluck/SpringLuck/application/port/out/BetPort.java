package com.spingluck.SpringLuck.application.port.out;

import com.spingluck.SpringLuck.application.domain.model.Bet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BetPort {
    void save(Bet bet);
    Optional<Bet> findById(UUID id);
    Optional<List<Bet>> findAll();
}
