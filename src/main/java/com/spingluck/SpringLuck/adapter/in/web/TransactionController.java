package com.spingluck.SpringLuck.adapter.in.web;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.port.in.TransactionUseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {

    @Autowired
    private final TransactionUseCase transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        Optional<List<Transaction>> transactions = transactionService.getAllTransactions();
        return transactions.isPresent() ? ResponseEntity.ok(transactions.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable UUID id){
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.isPresent() ? ResponseEntity.ok(transaction.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable UUID userId){
        Optional<List<Transaction>> transactionsForUser = transactionService.getAllUserTransaction(userId);
        return transactionsForUser.isPresent() ? ResponseEntity.ok(transactionsForUser.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction){
        try {
            Optional<Transaction> createdTransaction = transactionService.createTransaction(transaction);
            return createdTransaction.map(value -> ResponseEntity.created(null).body(value)).orElseGet(() -> ResponseEntity.notFound().build());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
