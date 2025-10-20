package com.spingluck.SpringLuck.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@AllArgsConstructor
public class Transaction {

    private int id;
    private Double amount;
    private Bet bet;
    private TransactionType type;
    private Date date;
}

