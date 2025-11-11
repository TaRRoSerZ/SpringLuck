package com.spingluck.SpringLuck.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class Transaction {
    private UUID id;
    private Double amount;
    private UUID betId;
    private UUID userId;
    private String stripeIntentId;
    private TransactionType type;
    private TransactionStatus status;
    private Date date;
}

