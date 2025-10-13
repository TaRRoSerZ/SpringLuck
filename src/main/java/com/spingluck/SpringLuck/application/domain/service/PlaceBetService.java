package com.spingluck.SpringLuck.application.domain.service;

import com.spingluck.SpringLuck.application.domain.model.*;
import com.spingluck.SpringLuck.application.port.in.PlaceBetCommand;
import com.spingluck.SpringLuck.application.port.in.PlaceBetUseCase;
import com.spingluck.SpringLuck.application.port.out.LoadUserPort;
import com.spingluck.SpringLuck.application.port.out.SaveBetPort;
import com.spingluck.SpringLuck.application.port.out.SaveTransactionPort;
import com.spingluck.SpringLuck.application.port.out.UpdateUserPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
public class PlaceBetService implements PlaceBetUseCase {

    private final LoadUserPort loadUserPort;
    private final UpdateUserPort updateUserPort;
    private final SaveBetPort saveBetPort;
    private final SaveTransactionPort saveTransactionPort;

    @Override
    public void placeBet(PlaceBetCommand command) {
        User user = loadUserPort.loadUserById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getBalance() < command.amount()) {
            throw new IllegalStateException("Insufficient balance");
        }

        user.setBalance(user.getBalance() - command.amount());
        updateUserPort.updateUser(user);

        Bet bet = new Bet();
        bet.setUserId(user.getId());
        bet.setAmount(command.amount());
        bet.setGame(command.game());
        bet.setStatus(BetStatus.PENDING);
        saveBetPort.saveBet(bet);

        saveTransactionPort.saveTransaction(
                new Transaction(null, user.getId(), -command.amount(), TransactionType.BET,
                        TransactionStatus.COMMITTED, LocalDateTime.now()
        ));
    }
}
