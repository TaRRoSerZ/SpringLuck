package com.spingluck.SpringLuck.application.domain.service;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.port.in.TransactionUseCase;
import com.spingluck.SpringLuck.application.port.out.TransactionPort;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;


@AllArgsConstructor
public class TransactionService implements TransactionUseCase {

    private TransactionPort transactionPort;


    @Override
    public Optional<Transaction> createTransaction(Transaction transaction) {
        return transactionPort.save(transaction);
    }

    @Override
    public Optional<List<Transaction>> getAllTransactions() {
        return transactionPort.findAll();
    }

    @Override
    public Optional<Transaction> getTransactionById(int id) {
        return transactionPort.findById(id);
    }
}
