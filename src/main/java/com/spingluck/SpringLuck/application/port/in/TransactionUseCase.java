package com.spingluck.SpringLuck.application.port.in;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;

import java.util.List;
import java.util.Optional;

public interface TransactionUseCase {

    Optional<Transaction> createTransaction(double amount, Bet bet, TransactionType type);
    Optional<List<Transaction>> getAllTransactions();
    Optional<Transaction> getTransactionById(int id);
}
