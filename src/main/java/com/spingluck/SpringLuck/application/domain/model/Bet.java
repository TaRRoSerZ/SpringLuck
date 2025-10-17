package com.spingluck.SpringLuck.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@AllArgsConstructor
public class Bet {

    private int id;
    private Double amount;
    private Date date;
    private Boolean isWinningBet;
}
