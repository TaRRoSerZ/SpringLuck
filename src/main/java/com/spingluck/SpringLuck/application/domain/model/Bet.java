package com.spingluck.SpringLuck.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class Bet {

    private UUID id;
    private UUID userId;
    private Double amount;
    private Date date;
    private Boolean isWinningBet;
}

