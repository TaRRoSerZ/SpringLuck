package com.spingluck.SpringLuck.application.domain.service;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.port.in.BetUseCase;
import com.spingluck.SpringLuck.application.port.out.BetPort;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;


@AllArgsConstructor
public class BetService implements BetUseCase {

    private BetPort betPort;

    @Override
    public void placeBet(Bet bet) {
        betPort.save(bet);
    }

    @Override
    public Optional<List<Bet>> getAllBets() {
        return betPort.findAll();
    }

    @Override
    public Optional<Bet> getBetById(int id) {
        return betPort.findById(id);
    }
}
