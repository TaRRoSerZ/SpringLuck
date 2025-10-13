package com.spingluck.SpringLuck.application.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Bet {
    private Long id;
    private Long userId;
    private Double amount;
    private String game;
    private BetStatus status ;
    private Double gain;
}