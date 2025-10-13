package com.spingluck.SpringLuck.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
public class Transaction {
    private Long id;
    private Long userId;
    private double amount;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime timestamp;

}