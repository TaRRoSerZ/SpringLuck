package com.spingluck.SpringLuck.application.port.in;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionUseCase {

    Optional<Transaction> createTransaction(Transaction transaction);
    Optional<List<Transaction>> getAllTransactions();
    Optional<List<Transaction>> getAllUserTransaction(UUID id);
    Optional<Transaction> getTransactionById(UUID id);
}
