package com.spingluck.SpringLuck.application.port.out;

import com.spingluck.SpringLuck.application.domain.model.Bet;

import java.util.List;
import java.util.Optional;

public interface BetPort {
    void save(Bet bet);
    Optional<Bet> findById(Long id);
    Optional<List<Bet>> findAll();
}
