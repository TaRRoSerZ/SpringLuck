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
import java.util.UUID;

import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Test
    void getAllTransactions() {

        Transaction t1 = new Transaction(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"), 500.0, UUID.fromString("260e8400-e29b-41d4-a716-446655440000"), UUID.fromString("360e8400-e29b-41d4-a716-446655440000"), TransactionType.DEPOSIT, new Date());
        Transaction t2 = new Transaction(UUID.fromString("170e8400-e29b-41d4-a716-446655440000"), 200.0, UUID.fromString("270e8400-e29b-41d4-a716-446655440000"), UUID.fromString("370e8400-e29b-41d4-a716-446655440000"), TransactionType.WITHDRAW, new Date());

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
        Transaction t1 = new Transaction(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"), 500.0, UUID.fromString("260e8400-e29b-41d4-a716-446655440000"), UUID.fromString("360e8400-e29b-41d4-a716-446655440000"), TransactionType.DEPOSIT, new Date());
        TransactionPort transactionPortStub = mock(TransactionPort.class);
        when(transactionPortStub.findById(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.of(t1));

        TransactionUseCase transactionService = new TransactionService(transactionPortStub);
        Optional<Transaction> transaction = transactionService.getTransactionById(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"));
        if (transaction.isEmpty()) {
            return;
        }
        Assertions.assertEquals(Optional.of(t1), transaction);
        Assertions.assertEquals(transaction.get().getAmount(), t1.getAmount());
        Assertions.assertEquals(transaction.get().getBetId(), t1.getBetId());
    }

    @Test
    void createTransaction() {
        Transaction t1 = new Transaction(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"), 500.0, UUID.fromString("260e8400-e29b-41d4-a716-446655440000"),UUID.fromString("360e8400-e29b-41d4-a716-446655440000"), TransactionType.DEPOSIT, new Date());
        TransactionPort transactionPortStub = mock(TransactionPort.class);

        TransactionUseCase transactionService = new TransactionService(transactionPortStub);
        transactionService.createTransaction(t1);
        verify(transactionPortStub).save(t1);
    }

    @Test
    void getTransactionByUserId() {
        Transaction t1 = new Transaction(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"), 500.0, UUID.fromString("260e8400-e29b-41d4-a716-446655440000"), UUID.fromString("360e8400-e29b-41d4-a716-446655440000"), TransactionType.DEPOSIT, new Date());
        Transaction t2 = new Transaction(UUID.fromString("170e8400-e29b-41d4-a716-446655440000"), 500.0, UUID.fromString("270e8400-e29b-41d4-a716-446655440000"), UUID.fromString("360e8400-e29b-41d4-a716-446655440000"), TransactionType.DEPOSIT, new Date());
        Optional<List<Transaction>> transactionsBd = Optional.of(List.of(t1, t2));
        TransactionPort transactionPortStub = mock(TransactionPort.class);
        when(transactionPortStub.findTransactionsByUserId(UUID.fromString("360e8400-e29b-41d4-a716-446655440000"))).thenReturn(transactionsBd);

        TransactionUseCase transactionService = new TransactionService(transactionPortStub);
        Optional<List<Transaction>> transactions = transactionService.getAllUserTransaction(UUID.fromString("360e8400-e29b-41d4-a716-446655440000"));
        if (transactions.isEmpty()) {
            return;
        }
        Assertions.assertArrayEquals(transactions.get().toArray(), transactionsBd.get().toArray());
    }
}
