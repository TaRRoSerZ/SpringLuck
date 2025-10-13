package com.spingluck.SpringLuck.application.port.out;

import com.spingluck.SpringLuck.application.domain.model.Bet;

public interface SaveBetPort {
    void saveBet(Bet bet);
}
