package com.spingluck.SpringLuck.application.domain.service;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionStatus;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.port.in.TransactionUseCase;
import com.spingluck.SpringLuck.application.port.out.TransactionPort;
import com.spingluck.SpringLuck.application.port.out.UserPort;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@AllArgsConstructor
public class TransactionService implements TransactionUseCase {

    private TransactionPort transactionPort;
    private UserPort userPort;


    @Override
    public Optional<Transaction> createTransaction(Transaction transaction) {
        return transactionPort.save(transaction);
    }

    @Override
    public Optional<List<Transaction>> getAllTransactions() {
        return transactionPort.findAll();
    }

    @Override
    public Optional<List<Transaction>> getAllUserTransaction(UUID userId) {
        return transactionPort.findTransactionsByUserId(userId);
    }

    @Override
    public Optional<Transaction> getTransactionById(UUID id) {
        return transactionPort.findById(id);
    }

    @Override
    public void confirmPayment(String intentId, String userEmail) {
        Optional<Transaction> optTx = transactionPort.findByStripeIntentId(intentId);

        if (optTx.isEmpty()) {
            System.out.println("Aucune transaction trouvée pour StripeIntentId : " + intentId);
            return;
        }

        Transaction tx = optTx.get();

        if (tx.getStatus() == TransactionStatus.CONFIRMED) {
            System.out.println("Transaction déjà confirmée : " + intentId);
            return;
        }

        try {
            userPort.updateBalance(userEmail, tx.getAmount());
            System.out.println("Paiement confirmé, balance mise à jour pour user " + userEmail);
            transactionPort.updateStatusByStripeId(intentId, TransactionStatus.CONFIRMED);

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la balance : " + e.getMessage());
            transactionPort.updateStatusByStripeId(intentId, TransactionStatus.FAILED);

        }

    }



}
