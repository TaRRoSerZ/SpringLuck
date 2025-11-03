package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.domain.service.BetService;
import com.spingluck.SpringLuck.application.domain.service.TransactionService;
import com.spingluck.SpringLuck.application.port.in.BetUseCase;
import com.spingluck.SpringLuck.application.port.in.TransactionUseCase;
import com.spingluck.SpringLuck.application.port.out.BetPort;
import com.spingluck.SpringLuck.application.port.out.TransactionPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Test
    void getAllTransactions() {
        Bet bet1 = new Bet(1, 100.0, new Date(), false);
        Bet bet2 = new Bet(2, 700.0, new Date(), true);

        Transaction t1 = new Transaction(1, 500.0, 1, TransactionType.DEPOSIT, new Date());
        Transaction t2 = new Transaction(2, 200.0, 2, TransactionType.WITHDRAWAL, new Date());

        Optional<List<Transaction>> transactionsBd = Optional.of(List.of(t1, t2));

        TransactionPort transactionPort = mock(TransactionPort.class);
        when(transactionPort.findAll()).thenReturn(transactionsBd);

        TransactionUseCase transactionService = new TransactionService(transactionPort);

        Optional<List<Transaction>> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return;
        }
        Assertions.assertArrayEquals(transactions.get().toArray(), transactionsBd.get().toArray());
    }

    @Test
    void getTransactionById() {
        Bet bet1 = new Bet(1, 100.0, new Date(), false);
        Transaction t1 = new Transaction(1, 500.0, 1, TransactionType.DEPOSIT, new Date());
        TransactionPort transactionPortStub = mock(TransactionPort.class);
        when(transactionPortStub.findById(1)).thenReturn(Optional.of(t1));

        TransactionUseCase transactionService = new TransactionService(transactionPortStub);
        Optional<Transaction> transaction = transactionService.getTransactionById(1);
        if (transaction.isEmpty()) {
            return;
        }
        Assertions.assertEquals(Optional.of(t1), transaction);
        Assertions.assertEquals(transaction.get().getAmount(), t1.getAmount());
        Assertions.assertEquals(transaction.get().getBetId(), t1.getBetId());
    }

    @Test
    void createTransaction() {
        Bet bet1 = new Bet(1, 100.0, new Date(), false);
        Transaction t1 = new Transaction(1, 500.0, 1, TransactionType.DEPOSIT, new Date());
        TransactionPort transactionPortStub = mock(TransactionPort.class);

        TransactionUseCase transactionService = new TransactionService(transactionPortStub);
        transactionService.createTransaction(t1);
        verify(transactionPortStub).save(t1);

    }
}
