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
    private TransactionType type;
    private Date date;
}

