package com.spingluck.SpringLuck.application.domain.model;

import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private Long userId;
    private double amount;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime timestamp;
}