package com.spingluck.SpringLuck.application.port.out;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;

import java.util.List;
import java.util.Optional;

public interface TransactionPort {
    Optional<Transaction> save(Transaction transaction);
    Optional<Transaction> findById(int id);
    Optional<List<Transaction>> findAll();
}
